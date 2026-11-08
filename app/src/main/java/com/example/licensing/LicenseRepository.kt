package com.example.licensing

import android.content.Context
import com.example.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class LicenseRepository(context: Context) {
    private val appContext = context.applicationContext
    private val prefs = appContext.getSharedPreferences("waqfa_signed_license", Context.MODE_PRIVATE)
    private val device = DeviceIdentity(appContext)
    private val _state = MutableStateFlow(check())
    val state: StateFlow<LicenseState> = _state.asStateFlow()

    fun activate(activationCode: String): LicenseState {
        val decoded = LicenseCodec.decode(activationCode)
        val outcome = when {
            decoded == null -> LicenseState(LicenseStatus.INVALID_SIGNATURE)
            else -> evaluate(decoded)
        }
        if (outcome.isActive) prefs.edit().putString(KEY_LICENSE, activationCode.trim()).apply()
        _state.value = outcome
        return outcome
    }

    /** Kept for backwards compatibility; License ID is now created automatically by the app. */
    fun activate(licenseId: String, activationCode: String): LicenseState = activate(activationCode)

    fun refresh(): LicenseState = check().also { _state.value = it }

    fun recoveryRequest(licenseId: String): String {
        val requestId = UUID.randomUUID().toString().replace("-", "").take(12)
        val request = "WAQFA-R1|${licenseId.trim()}|${device.keyHash()}|$requestId"
        prefs.edit().putString(KEY_LAST_RECOVERY, request).apply()
        return request
    }

    /** A stable, installation-bound request code. The customer never has to type an ID. */
    fun preActivationRequest(): String {
        val existing = prefs.getString(KEY_PRE_ACTIVATION, null)
        if (existing != null) return existing
        val hash = device.keyHash()
        val automaticLicenseId = "WAQFA-" + hash.take(10).uppercase()
        val code = "WAQFA-R1|$automaticLicenseId|$hash|${hash.takeLast(8).uppercase()}"
        prefs.edit().putString(KEY_PRE_ACTIVATION, code).apply()
        return code
    }

    fun devicePublicKeyForRecovery(): String = device.publicKeyForRecovery()

    private fun check(): LicenseState {
        val code = prefs.getString(KEY_LICENSE, null) ?: return LicenseState(LicenseStatus.MISSING)
        return LicenseCodec.decode(code)?.let(::evaluate) ?: LicenseState(LicenseStatus.INVALID_SIGNATURE)
    }

    private fun evaluate(license: SignedLicense): LicenseState {
        val now = System.currentTimeMillis()
        val largestSeen = prefs.getLong(KEY_LARGEST_SEEN_TIME, 0L)
        val status = LicensePolicy.evaluate(license, BuildConfig.WAQFA_LICENSE_PUBLIC_KEY, device.keyHash(), now, largestSeen)
        if (status != LicenseStatus.ACTIVE) return LicenseState(status, license)
        if (now > largestSeen) prefs.edit().putLong(KEY_LARGEST_SEEN_TIME, now).apply()
        return LicenseState(LicenseStatus.ACTIVE, license)
    }

    companion object {
        private const val KEY_LICENSE = "license_token"
        private const val KEY_LARGEST_SEEN_TIME = "largest_seen_time"
        private const val KEY_LAST_RECOVERY = "last_recovery"
        private const val KEY_PRE_ACTIVATION = "pre_activation_request"
    }
}
