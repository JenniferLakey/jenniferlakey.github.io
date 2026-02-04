package com.example.inventoryapp.utils;

import android.text.TextUtils;

/**
 * ValidationUtils
 * ---------------
 * Utility class for validating user input across the app.
 * Provides simple checks for usernames, passwords, item fields, metadata, and phone numbers.
 *
 * Note:
 * - These are basic validations for course scope.
 * - In production, stronger rules should be considered (e.g., regex for usernames,
 *   password complexity requirements, locale-specific phone number formats).
 */
public class ValidationUtils {

    // --- User validation ---

    /**
     * Validate username: must not be empty and must have at least 3 characters.
     */
    public static boolean isValidUsername(String username) {
        return !TextUtils.isEmpty(username) && username.length() >= 3;
    }

    /**
     * Validate password: must not be empty and must have at least 6 characters.
     */
    public static boolean isValidPassword(String password) {
        return !TextUtils.isEmpty(password) && password.length() >= 6;
    }

    /**
     * Confirm that two password strings match.
     */
    public static boolean doPasswordsMatch(String p1, String p2) {
        return p1 != null && p1.equals(p2);
    }

    /**
     * Validate phone number: optional field.
     * - Returns true if null or empty (user chose not to provide).
     * - If provided, must be at least 7 digits and only contain digits/spaces/plus signs.
     * - This is a basic check; production apps should use locale-specific regex or libraries.
     */
    public static boolean isValidPhoneNumber(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return true; // optional, so empty is valid
        }
        String trimmed = phone.trim();
        // Basic pattern: digits, spaces, plus sign allowed
        return trimmed.matches("^[+]?[0-9 ]{7,}$");
    }

    // --- Inventory validation ---

    /**
     * Validate item name: must not be empty.
     */
    public static boolean isValidItemName(String name) {
        return !TextUtils.isEmpty(name);
    }

    /**
     * Validate quantity: must be a non-negative integer.
     */
    public static boolean isValidQuantity(int qty) {
        return qty >= 0;
    }

    /**
     * Validate category: can be null or empty, but if provided must not be just whitespace.
     */
    public static boolean isValidCategory(String category) {
        return category == null || category.trim().length() > 0;
    }

    /**
     * Validate metadata fields (description/location).
     * - Optional by design: returns true if null or empty.
     * - If provided, must not be just whitespace and must be within a reasonable length.
     */
    public static boolean isValidMetadata(String text) {
        if (TextUtils.isEmpty(text)) {
            return true; // optional, so empty is valid
        }
        String trimmed = text.trim();
        return trimmed.length() > 0 && trimmed.length() <= 255;
    }
}