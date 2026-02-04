package com.example.inventoryapp;

import android.app.Application;

import com.example.inventoryapp.db.DbExecutor;

/**
 * InventoryApp
 * ------------
 * Custom Application class for global app state.
 * Best practice: initialize singletons and clean up resources here.
 */
public class InventoryApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize global resources here (e.g., logging, analytics)
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        // Shut down executors to avoid resource leaks
        DbExecutor.IO.shutdown();
    }
}
