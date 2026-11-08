package com.example

import android.util.Base64
import com.example.licensing.LicenseCodec
import com.example.licensing.LicensePolicy
import com.example.licensing.LicenseStatus
import com.example.licensing.SignedLicense
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.security.KeyPairGenerator
import java.security.Signature
import java.security.spec.ECGenParameterSpec

@RunWith(RobolectricTestRunner::class)
class LicensePolicyTest {
    private val keyPair = KeyPairGenerator.getInstance("EC").apply { initialize(ECGenParameterSpec("secp256r1")) }.generateKeyPair()
    private val publicKey = Base64.encodeToString(keyPair.public.encoded, Base64.NO_WRAP)
    private val now = 1_800_000_000_000L

    private fun signed(device: String = "device-a", expires: Long = now + 365L * 24 * 60 * 60 * 1000): SignedLicense {
        val unsigned = SignedLicense(1, "LIC-1", now, expires, device, 0, 3, "premium", "nonce", "")
        val signature = Signature.getInstance("SHA256withECDSA").apply { initSign(keyPair.private); update(LicenseCodec.canonical(unsigned).toByteArray()) }.sign()
        return unsigned.copy(signature = Base64.encodeToString(signature, Base64.NO_WRAP))
    }

    @Test fun activation_accepts_valid_signed_license() = assertEquals(LicenseStatus.ACTIVE, LicensePolicy.evaluate(signed(), publicKey, "device-a", now, now))
    @Test fun modified_code_is_rejected() = assertEquals(LicenseStatus.INVALID_SIGNATURE, LicensePolicy.evaluate(signed().copy(expiresAtEpochMs = now + 999999999), publicKey, "device-a", now, now))
    @Test fun expiration_locks_license() = assertEquals(LicenseStatus.EXPIRED, LicensePolicy.evaluate(signed(expires = now - 1), publicKey, "device-a", now, now))
    @Test fun replacement_device_requires_new_signed_license() = assertEquals(LicenseStatus.WRONG_DEVICE, LicensePolicy.evaluate(signed(), publicKey, "device-b", now, now))
    @Test fun rollback_is_detected() = assertEquals(LicenseStatus.CLOCK_TAMPERED, LicensePolicy.evaluate(signed(), publicKey, "device-a", now, now + LicensePolicy.CLOCK_ROLLBACK_TOLERANCE_MS + 1))
}
