# WAQFA offline license manager

Compile with a local JDK: `javac WaqfaLicenseManager.java`.

Run once on the owner's protected computer:
`java WaqfaLicenseManager init owner-private.pem owner-public.txt`

Never share, commit, email, or place `owner-private.pem` in the APK. Build WAQFA with the contents of `owner-public.txt` as the Gradle property `waqfaLicensePublicKey`.

For a new purchase or replacement device, the customer sends the License ID and the generated recovery code. The owner verifies the original order, records the old authorization as revoked in their offline ledger, then issues a replacement using the device key hash embedded in the recovery code. Keep the original expiration timestamp to preserve the remaining annual validity.

Example issue command:
`java WaqfaLicenseManager issue owner-private.pem LIC-123 <device-key-hash> <original-expires-epoch-ms> 1 3 premium`

One active device is a policy enforced by the owner's issuance ledger. Because the app is offline, an already-issued license cannot be remotely revoked until it expires; do not claim immediate remote revocation or perfect prevention of code sharing.
