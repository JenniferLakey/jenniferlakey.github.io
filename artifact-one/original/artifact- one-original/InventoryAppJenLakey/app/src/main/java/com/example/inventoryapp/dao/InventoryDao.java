package com.example.inventoryapp.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import androidx.room.RawQuery;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.example.inventoryapp.model.Inventory;
import com.example.inventoryapp.model.ItemMetadata;
import com.example.inventoryapp.model.ItemWithMetadata;

import java.util.List;

/**
 * InventoryDao
 * ------------
 * Data access object for Inventory entity.
 * - Provides insert, update, delete, and retrieval operations.
 * - Uses LiveData for reactive observation in UI.
 * - Ensures updates are tied to primary key (itemId) so Room can persist changes.
 * - Always LEFT JOINs metadata when retrieving combined entities so items without metadata are included.
 */
@Dao
public interface InventoryDao {

    // --- Insert ---
    @Insert
    long insertItem(Inventory item);

    // --- Update ---
    // Standard update: Room matches on primary key (itemId).
    @Update
    int updateItem(Inventory item);

    // Quantity-only update: explicit query for efficiency.
    @Query("UPDATE inventory SET quantity = :newQty WHERE item_id = :itemId")
    int updateQuantity(int itemId, int newQty);

    // --- Delete ---
    @Delete
    int deleteItem(Inventory item);

    // --- Retrieval ---
    // Synchronous fetch by ID (used in repository logic).
    @Query("SELECT * FROM inventory WHERE item_id = :itemId LIMIT 1")
    Inventory getItemByIdSync(int itemId);

    // Relation: LiveData so detail/edit screens auto-refresh when DB changes.
    // Uses LEFT JOIN internally via Room relation mapping to include items even if metadata is missing.
    @Transaction
    @Query("SELECT * FROM inventory WHERE item_id = :itemId")
    LiveData<ItemWithMetadata> getItemWithMetadata(int itemId);

    // --- Dynamic filtering ---
    // Supports flexible queries with sorting/filtering.
    // Repository builds queries with LEFT JOIN to ensure items without metadata are included.
    @RawQuery(observedEntities = {Inventory.class, ItemMetadata.class})
    LiveData<List<Inventory>> filterWithRaw(SimpleSQLiteQuery query);

    // --- Utility ---
    // Count items by name (used for uniqueness validation).
    @Query("SELECT COUNT(*) FROM inventory WHERE item_name = :itemName")
    int countByName(String itemName);
}