package com.example.licensing

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.MessageDigest
import java.security.spec.ECGenParameterSpec
import java.util.UUID

/**
 * A per-install key, not an IMEI/serial. Loss of this key is recoverable by issuing a new signed license.
 */
class DeviceIdentity(private val context: Context) {
    private val prefs = context.getSharedPreferences("waqfa_license_identity", Context.MODE_PRIVATE)

    fun keyHash(): String {
        val bytes = publicKeyBytes() ?: fallbackInstallRandom().toByteArray()
        return MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) }
    }

    fun publicKeyForRecovery(): String = publicKeyBytes()?.let {
        Base64.encodeToString(it, Base64.NO_WRAP)
    } ?: ""

    private fun publicKeyBytes(): ByteArray? = try {
        val alias = "waqfa_installation_key_v1"
        val ks = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        if (!ks.containsAlias(alias)) {
            val generator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore")
            val spec = KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY)
                .setAlgorithmParameterSpec(ECGenParameterSpec("secp256r1"))
                .setDigests(KeyProperties.DIGEST_SHA256)
                .build()
            generator.initialize(spec)
            generator.generateKeyPair()
        }
        ks.getCertificate(alias)?.publicKey?.encoded
    } catch (_: Exception) { null }

    private fun fallbackInstallRandom(): String {
        val existing = prefs.getString("fallback_install_random", null)
        if (existing != null) return existing
        return UUID.randomUUID().toString().also { prefs.edit().putString("fallback_install_random", it).apply() }
    }
}
