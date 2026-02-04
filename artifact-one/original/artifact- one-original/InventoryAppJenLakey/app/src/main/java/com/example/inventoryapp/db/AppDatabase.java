package com.example.inventoryapp.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.inventoryapp.dao.InventoryDao;
import com.example.inventoryapp.dao.ItemMetadataDao;
import com.example.inventoryapp.dao.UserDao;
import com.example.inventoryapp.model.Inventory;
import com.example.inventoryapp.model.ItemMetadata;
import com.example.inventoryapp.model.User;

/**
 * AppDatabase
 * -----------
 * Central Room database definition for the Inventory app.
 * - Holds DAOs for Inventory, ItemMetadata, and User entities.
 * - Implements a thread-safe singleton pattern to provide a single database instance.
 * - Schema is considered final: no migrations are defined.
 *
 * Best practice:
 * - Disallow main-thread queries in production.
 * - Use fallbackToDestructiveMigration() during development to rebuild DB automatically
 *   if schema changes (e.g., adding phoneNumber to User).
 */
@Database(
        entities = {Inventory.class, ItemMetadata.class, User.class},
        version = 1, // schema version remains 1 since migrations are not planned
        exportSchema = false // disable schema export since migrations are not planned
)
public abstract class AppDatabase extends RoomDatabase {

    // --- Singleton instance ---
    private static volatile AppDatabase INSTANCE;

    // --- DAO accessors ---
    public abstract InventoryDao inventoryDao();
    public abstract ItemMetadataDao itemMetadataDao();
    public abstract UserDao userDao();

    /**
     * Returns the singleton instance of AppDatabase.
     * Uses double-checked locking for thread safety.
     *
     * @param context Application context
     * @return AppDatabase instance
     */
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "inventory_db"
                            )
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}