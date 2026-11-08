package com.example.licensing

data class SignedLicense(
    val version: Int,
    val licenseId: String,
    val issuedAtEpochMs: Long,
    val expiresAtEpochMs: Long,
    val deviceKeyHash: String,
    val transferCount: Int,
    val maxTransfers: Int,
    val edition: String,
    val nonce: String,
    val signature: String
)

enum class LicenseStatus {
    ACTIVE, MISSING, INVALID_SIGNATURE, WRONG_DEVICE, EXPIRED, CLOCK_TAMPERED, OWNER_KEY_NOT_CONFIGURED
}

data class LicenseState(val status: LicenseStatus, val license: SignedLicense? = null) {
    val isActive: Boolean get() = status == LicenseStatus.ACTIVE
}
