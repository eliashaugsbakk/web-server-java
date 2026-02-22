package no.eliashaugsbakk.uploader.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Generation of authentication tokens.
 */
public class AuthUtils {
  final String chrs = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
  static final SecureRandom secureRandom;

  static {
    try {
      secureRandom = SecureRandom.getInstanceStrong();
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  public String generateAuthKey(int keyLength) {
    return secureRandom
        .ints(keyLength, 0, chrs.length())
        .mapToObj(chrs::charAt)
        .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
        .toString();
  }
}
