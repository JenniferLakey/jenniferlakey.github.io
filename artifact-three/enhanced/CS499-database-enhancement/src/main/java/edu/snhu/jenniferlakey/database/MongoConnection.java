package edu.snhu.jenniferlakey.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

/**
 * Provides a centralized, reusable connection manager for the MongoDB database.
 * 
 * This class implements a simple singleton-style pattern to ensure that the
 * application uses a single MongoClient instance throughout its lifecycle.
 * 
 * Responsibilities:
 * Establish a connection to the MongoDB server
 * Provide access to the configured database
 * Allow controlled shutdown of the MongoClient
 *
 * This replaces the in-memory storage used in the original CS-320 project
 * and serves as the foundation for the enhanced persistence layer.
 */

public class MongoConnection {

    // Connection string for the local MongoDB instance
    private static final String CONNECTION_STRING = "mongodb://localhost:27017";

    // Name of the database used by this application
    private static final String DATABASE_NAME = "cs320";

    // Shared MongoClient instance (lazy-loaded)
    private static MongoClient client;

    // Private constructor prevents instantiation
    private MongoConnection() {
    }

    /**
     * Returns a reference to the application's MongoDatabase instance.
     * 
     * If the MongoClient has not yet been created, this method initializes it
     * using the configured connection string. Subsequent calls reuse the same
     * client instance.
     *
     * @return the MongoDatabase object representing the application's database
     */
    public static MongoDatabase getDatabase() {
        if (client == null) {
            client = MongoClients.create(CONNECTION_STRING);
        }
        return client.getDatabase(DATABASE_NAME);
    }

    /**
     * Closes the MongoClient connection if it exists.
     * 
     * This method is useful for test cleanup or controlled application shutdown.
     * After closing, the client reference is reset so it can be reinitialized
     * later.
     */
    public static void close() {
        if (client != null) {
            client.close();
            client = null;
        }
    }
}