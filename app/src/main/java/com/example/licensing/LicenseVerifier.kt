package com.example.licensing

import android.util.Base64
import java.security.KeyFactory
import java.security.Signature
import java.security.spec.X509EncodedKeySpec

object LicenseVerifier {
    fun verify(license: SignedLicense, publicKeyBase64: String): Boolean = try {
        val keyBytes = Base64.decode(publicKeyBase64, Base64.DEFAULT)
        val publicKey = KeyFactory.getInstance("EC").generatePublic(X509EncodedKeySpec(keyBytes))
        val verifier = Signature.getInstance("SHA256withECDSA")
        verifier.initVerify(publicKey)
        verifier.update(LicenseCodec.canonical(license).toByteArray(Charsets.UTF_8))
        verifier.verify(Base64.decode(license.signature, Base64.DEFAULT))
    } catch (_: Exception) { false }
}
