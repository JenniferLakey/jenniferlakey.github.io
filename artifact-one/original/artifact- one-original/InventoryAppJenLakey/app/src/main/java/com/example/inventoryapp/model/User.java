package com.example.inventoryapp.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * User
 * ----
 * Entity representing a user account in the database.
 * Includes:
 * - username (unique, required)
 * - password hash (for authentication; never store plain text)
 * - role (authorization, e.g., admin, staff, owner, auditor)
 * - phone number (optional; used for SMS alerts when inventory is low)
 *
 * Best practice:
 * - Enforce unique usernames via index.
 * - Store sensitive data securely (passwords hashed).
 * - Keep optional fields nullable to avoid forcing unnecessary input.
 */
@Entity(
        tableName = "users",
        indices = {@Index(value = {"username"}, unique = true)} // enforce unique usernames
)
public class User {

    // --- Primary key ---
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_id")
    public int userId;

    // --- Username (unique, required) ---
    @ColumnInfo(name = "username")
    public String username;

    // --- Password hash (required) ---
    @ColumnInfo(name = "password_hash")
    public String passwordHash;

    // --- Role (authorization, required) ---
    @ColumnInfo(name = "role")
    public String role; // e.g., admin, staff, owner, auditor

    // --- Phone number (optional, used for SMS alerts) ---
    @ColumnInfo(name = "phone_number")
    public String phoneNumber; // may be null if user has not enabled SMS alerts
}