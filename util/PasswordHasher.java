package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHasher {

    public static String hashPassword(String password) {

        try {
            MessageDigest messageDigest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hashBytes =
                    messageDigest.digest(
                            password.getBytes(StandardCharsets.UTF_8)
                    );

            StringBuilder hash = new StringBuilder();

            for (byte b : hashBytes) {
                hash.append(String.format("%02x", b));
            }

            return hash.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}