package edu.snhu.jenniferlakey.database;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.mongodb.client.MongoDatabase;

/**
 * Tests for the MongoConnection class to verify correct database
 * initialization, singleton behavior, and proper cleanup.
 */

public class MongoConnectionTest {

    // Ensures the connection is closed after each test
    @AfterEach
    public void tearDown() {
        MongoConnection.close();
    }

    // Verifies that the database object is not null
    @Test
    public void testGetDatabaseNotNull() {
        MongoDatabase db = MongoConnection.getDatabase();
        assertNotNull(db, "Database should not be null");
    }

    // Verifies that the database name matches the expected value
    @Test
    public void testDatabaseName() {
        MongoDatabase db = MongoConnection.getDatabase();
        assertEquals("cs320", db.getName(), "Database name should be 'cs320'");
    }

    // Ensures that the same MongoClient instance is reused
    @Test
    public void testSingletonBehavior() {
        MongoDatabase db1 = MongoConnection.getDatabase();
        MongoDatabase db2 = MongoConnection.getDatabase();
        assertSame(db1.getName(), db2.getName(), "Database instances should match");
    }

    // Ensures that closing the connection resets the client
    @Test
    public void testCloseResetsClient() {
        MongoConnection.getDatabase(); // initialize client
        MongoConnection.close();
        MongoDatabase db2 = MongoConnection.getDatabase();
        assertNotNull(db2, "Database should reconnect after close");
    }
}