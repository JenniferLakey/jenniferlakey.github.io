package com.example.inventoryapp.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.OnConflictStrategy;

import com.example.inventoryapp.model.User;

/**
 * UserDao
 * -------
 * Data Access Object (DAO) for User entities.
 * Provides operations for user registration, lookup, authentication, and phone number updates.
 * Note:
 * - User entity stores password as a hash (passwordHash).
 * - Phone number is optional and may be null if SMS alerts are not enabled.
 */
@Dao
public interface UserDao {

    // --- Insert operations ---
    @Insert(onConflict = OnConflictStrategy.ABORT) // prevent duplicate usernames
    long insertUser(User user);

    // --- Retrieval operations ---
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    User findByUsername(String username);

    @Query("SELECT * FROM users WHERE user_id = :userId LIMIT 1")
    User findById(int userId);

    // --- Utility operations ---
    @Query("SELECT COUNT(*) FROM users WHERE username = :username")
    int countByUsername(String username);

    // --- Update operations ---
    /**
     * Update a user's phone number.
     * Stores null if phone number is cleared.
     *
     * @param userId      ID of the user to update
     * @param phoneNumber new phone number (nullable)
     * @return number of rows affected
     */
    @Query("UPDATE users SET phone_number = :phoneNumber WHERE user_id = :userId")
    int updatePhoneNumber(int userId, String phoneNumber);
}