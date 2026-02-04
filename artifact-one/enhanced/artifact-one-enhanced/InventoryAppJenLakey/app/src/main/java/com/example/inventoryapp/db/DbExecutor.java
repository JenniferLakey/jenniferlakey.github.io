package com.example.inventoryapp.db;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * DbExecutor
 * ----------
 * Provides a centralized ExecutorService for database operations.
 * Ensures queries run off the main thread to prevent UI blocking.
 * Best practice: use a fixed thread pool for scalability if multiple DB tasks
 * may run concurrently.
 */
public class DbExecutor {

    // --- Single-thread executor for sequential DB operations ---
    public static final ExecutorService IO = Executors.newSingleThreadExecutor();

    // Optional improvement: fixed thread pool for parallel DB tasks
    // public static final ExecutorService IO = Executors.newFixedThreadPool(4);

    // Prevent instantiation
    private DbExecutor() {}
}

