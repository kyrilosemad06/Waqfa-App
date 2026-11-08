package com.example.licensing

object LicensePolicy {
    fun evaluate(license: SignedLicense, publicKey: String, deviceHash: String, now: Long, largestSeen: Long): LicenseStatus = when {
        publicKey.isBlank() -> LicenseStatus.OWNER_KEY_NOT_CONFIGURED
        !LicenseVerifier.verify(license, publicKey) -> LicenseStatus.INVALID_SIGNATURE
        license.deviceKeyHash != deviceHash -> LicenseStatus.WRONG_DEVICE
        now + CLOCK_ROLLBACK_TOLERANCE_MS < largestSeen -> LicenseStatus.CLOCK_TAMPERED
        now >= license.expiresAtEpochMs -> LicenseStatus.EXPIRED
        else -> LicenseStatus.ACTIVE
    }

    const val CLOCK_ROLLBACK_TOLERANCE_MS = 6 * 60 * 60 * 1000L
}
