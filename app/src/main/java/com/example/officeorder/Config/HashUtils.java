package com.example.officeorder.Config;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class HashUtils {

    public static String hashPasswordWithSalt(String password, String salt) {
        try {
            String passwordWithSalt = password + salt;

            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            digest.update(passwordWithSalt.getBytes());

            byte[] hashedBytes = digest.digest();
            return bytesToHex(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02X", b));
        }
        return result.toString();
    }

    public static String generateSalt() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        return bytesToHex(salt);
    }
}
