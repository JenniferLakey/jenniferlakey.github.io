package com.example.inventoryapp.utils;

import android.util.Base64;

import java.security.MessageDigest;
import java.security.SecureRandom;

/**
 * PasswordUtils
 * -------------
 * Utility class for hashing and verifying passwords.
 * Uses salted SHA-256 for course scope. ⚠ Replace with a stronger algorithm
 * (e.g., BCrypt, Argon2) in production for better security.
 */
public class PasswordUtils {

    /**
     * Hash a raw password using SHA-256 with a random salt.
     *
     * @param raw plain text password
     * @return Base64-encoded salt and digest, separated by ":"
     */
    public static String hash(String raw) {
        try {
            // Generate random salt
            byte[] salt = new byte[16];
            new SecureRandom().nextBytes(salt);

            // Compute SHA-256 digest with salt
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] digest = md.digest(raw.getBytes());

            // Encode salt and digest as Base64
            return Base64.encodeToString(salt, Base64.NO_WRAP) + ":" +
                    Base64.encodeToString(digest, Base64.NO_WRAP);
        } catch (Exception e) {
            throw new RuntimeException("Password hashing failed", e);
        }
    }

    /**
     * Verify a raw password against a stored salted hash.
     *
     * @param raw    plain text password to verify
     * @param stored stored hash string (salt:digest)
     * @return true if password matches, false otherwise
     */
    public static boolean verify(String raw, String stored) {
        try {
            // Split stored value into salt and digest
            String[] parts = stored.split(":");
            if (parts.length != 2) return false;

            byte[] salt = Base64.decode(parts[0], Base64.NO_WRAP);
            byte[] expected = Base64.decode(parts[1], Base64.NO_WRAP);

            // Compute digest with stored salt
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] actual = md.digest(raw.getBytes());

            // Constant-time comparison to prevent timing attacks
            if (actual.length != expected.length) return false;
            int diff = 0;
            for (int i = 0; i < actual.length; i++) {
                diff |= actual[i] ^ expected[i];
            }
            return diff == 0;
        } catch (Exception e) {
            return false;
        }
    }
}