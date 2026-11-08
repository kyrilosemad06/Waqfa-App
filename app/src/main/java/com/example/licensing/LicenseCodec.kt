package com.example.licensing

import android.util.Base64

object LicenseCodec {
    fun canonical(license: SignedLicense): String = listOf(
        "v=${license.version}",
        "licenseId=${license.licenseId}",
        "issuedAt=${license.issuedAtEpochMs}",
        "expiresAt=${license.expiresAtEpochMs}",
        "deviceKeyHash=${license.deviceKeyHash}",
        "transferCount=${license.transferCount}",
        "maxTransfers=${license.maxTransfers}",
        "edition=${license.edition}",
        "nonce=${license.nonce}"
    ).joinToString("\n")

    fun encode(license: SignedLicense): String {
        val raw = canonical(license) + "\nsignature=${license.signature}"
        return Base64.encodeToString(raw.toByteArray(Charsets.UTF_8), Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }

    fun decode(token: String): SignedLicense? {
        return try {
            val raw = String(Base64.decode(token.trim(), Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING), Charsets.UTF_8)
            val map = raw.lineSequence().mapNotNull {
                val at = it.indexOf('=')
                if (at <= 0) null else it.substring(0, at) to it.substring(at + 1)
            }.toMap()
            val version = map["v"]?.toIntOrNull()
            val issuedAt = map["issuedAt"]?.toLongOrNull()
            val expiresAt = map["expiresAt"]?.toLongOrNull()
            val transfers = map["transferCount"]?.toIntOrNull()
            val maxTransfers = map["maxTransfers"]?.toIntOrNull()
            if (version == null || issuedAt == null || expiresAt == null || transfers == null || maxTransfers == null) null
            else SignedLicense(version, map["licenseId"] ?: return null, issuedAt, expiresAt, map["deviceKeyHash"] ?: return null, transfers, maxTransfers, map["edition"] ?: return null, map["nonce"] ?: return null, map["signature"] ?: return null)
        } catch (_: Exception) { null }
    }
}
