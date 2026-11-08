import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.*;
import java.security.spec.*;
import java.util.*;

/** Offline owner tool. Keep the generated private key outside source control and outside the APK. */
public final class WaqfaLicenseManager {
  static String b64(byte[] data) { return Base64.getEncoder().encodeToString(data); }
  static String url(byte[] data) { return Base64.getUrlEncoder().withoutPadding().encodeToString(data); }
  static String canonical(String id, long issued, long expires, String device, int transfers, int max, String edition, String nonce) {
    return String.join("\n", "v=1", "licenseId=" + id, "issuedAt=" + issued, "expiresAt=" + expires,
      "deviceKeyHash=" + device, "transferCount=" + transfers, "maxTransfers=" + max, "edition=" + edition, "nonce=" + nonce);
  }
  static PrivateKey privateKey(Path path) throws Exception {
    String pem = Files.readString(path).replaceAll("-----[^-]+-----", "").replaceAll("\\s", "");
    return KeyFactory.getInstance("EC").generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(pem)));
  }
  static void init(Path privateFile, Path publicFile) throws Exception {
    KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
    generator.initialize(new ECGenParameterSpec("secp256r1"));
    KeyPair pair = generator.generateKeyPair();
    Files.writeString(privateFile, "-----BEGIN PRIVATE KEY-----\n" + b64(pair.getPrivate().getEncoded()) + "\n-----END PRIVATE KEY-----\n", StandardOpenOption.CREATE_NEW);
    Files.writeString(publicFile, b64(pair.getPublic().getEncoded()) + "\n", StandardOpenOption.CREATE_NEW);
    System.out.println("Public key saved to " + publicFile + ". Build WAQFA with -PwaqfaLicensePublicKey=<that value>.");
  }
  static void issue(Path privateFile, String id, String deviceHash, long expires, int transfers, int maxTransfers, String edition) throws Exception {
    System.out.println(issueToken(privateFile, id, deviceHash, expires, transfers, maxTransfers, edition));
  }
  static String issueToken(Path privateFile, String id, String deviceHash, long expires, int transfers, int maxTransfers, String edition) throws Exception {
    if (transfers > maxTransfers) throw new IllegalArgumentException("Transfer limit exceeded");
    long issued = System.currentTimeMillis();
    String nonce = UUID.randomUUID().toString().replace("-", "");
    String canonical = canonical(id, issued, expires, deviceHash, transfers, maxTransfers, edition, nonce);
    Signature signer = Signature.getInstance("SHA256withECDSA");
    signer.initSign(privateKey(privateFile)); signer.update(canonical.getBytes(StandardCharsets.UTF_8));
    String token = url((canonical + "\nsignature=" + b64(signer.sign())).getBytes(StandardCharsets.UTF_8));
    return token;
  }
  static void usage() {
    System.out.println("init <private.pem> <public-key.txt>\nissue <private.pem> <license-id> <device-key-hash> <expires-epoch-ms> <transfer-count> <max-transfers> [edition]");
  }
  public static void main(String[] args) throws Exception {
    if (args.length == 0) { usage(); return; }
    if ("init".equals(args[0]) && args.length == 3) init(Path.of(args[1]), Path.of(args[2]));
    else if ("issue".equals(args[0]) && args.length >= 7) issue(Path.of(args[1]), args[2], args[3], Long.parseLong(args[4]), Integer.parseInt(args[5]), Integer.parseInt(args[6]), args.length > 7 ? args[7] : "premium");
    else usage();
  }
}
