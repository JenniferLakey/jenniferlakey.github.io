package com.example.inventoryapp.repository;

import android.content.Context;

import com.example.inventoryapp.db.AppDatabase;
import com.example.inventoryapp.dao.UserDao;
import com.example.inventoryapp.model.User;
import com.example.inventoryapp.utils.PasswordUtils;
import com.example.inventoryapp.utils.ValidationUtils;

/**
 * UserRepository
 * --------------
 * Repository layer that wraps UserDao operations.
 * Provides business logic for user registration and authentication.
 * Best practice:
 * - Keep DAOs pure (CRUD only).
 * - Put validation, hashing, and business rules here.
 * - Handle optional fields (like phone number) gracefully.
 */
public class UserRepository {
    private final UserDao userDao;

    public UserRepository(Context ctx) {
        userDao = AppDatabase.getInstance(ctx).userDao();
    }

    /**
     * Register a new user.
     * Enforces unique usernames, validates input, and hashes the password before persistence.
     *
     * @param username     chosen username
     * @param rawPassword  plain text password (will be hashed)
     * @param role         user role (e.g., "admin", "user")
     * @param phoneNumber  optional phone number for SMS alerts (may be null or empty)
     * @return true if insert succeeded
     * @throws Exception if validation fails or username already exists
     */
    public boolean register(String username, String rawPassword, String role, String phoneNumber) throws Exception {
        // --- Validation ---
        if (!ValidationUtils.isValidUsername(username)) {
            throw new Exception("Invalid username. Must be at least 3 characters.");
        }
        if (!ValidationUtils.isValidPassword(rawPassword)) {
            throw new Exception("Invalid password. Must be at least 6 characters.");
        }
        if (userDao.countByUsername(username) > 0) {
            throw new Exception("Username already exists.");
        }

        // --- Construct user entity ---
        User u = new User();
        u.username = username;
        u.passwordHash = PasswordUtils.hash(rawPassword);
        u.role = role;
        u.phoneNumber = (phoneNumber != null && !phoneNumber.trim().isEmpty())
                ? phoneNumber.trim()
                : null; // store null if not provided

        // --- Persist ---
        return userDao.insertUser(u) > 0;
    }

    /**
     * Authenticate a user by verifying password against stored hash.
     *
     * @param username    username to look up
     * @param rawPassword plain text password to verify
     * @return User object if authentication succeeds, null otherwise
     */
    public User authenticate(String username, String rawPassword) {
        User u = userDao.findByUsername(username);
        if (u == null) return null;
        return PasswordUtils.verify(rawPassword, u.passwordHash) ? u : null;
    }

    /**
     * Update a user's phone number.
     * Useful if SMS permission is granted after registration.
     *
     * @param userId      ID of the user to update
     * @param phoneNumber new phone number (nullable)
     * @return number of rows affected
     */
    public int updatePhoneNumber(int userId, String phoneNumber) {
        return userDao.updatePhoneNumber(userId,
                (phoneNumber != null && !phoneNumber.trim().isEmpty())
                        ? phoneNumber.trim()
                        : null);
    }
}