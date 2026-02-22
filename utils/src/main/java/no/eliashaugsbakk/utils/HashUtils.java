package no.eliashaugsbakk.utils;

import java.security.MessageDigest;
import java.util.HexFormat;

/**
 * SHA-256 logic
 */
public class HashUtils {
  public String calculateSHA256(byte[] data) throws Exception {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] encodedHash = digest.digest(data);
    return HexFormat.of().formatHex(encodedHash);
  }
}
