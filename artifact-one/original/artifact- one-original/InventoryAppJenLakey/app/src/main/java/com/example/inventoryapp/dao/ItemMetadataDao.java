package com.example.inventoryapp.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.OnConflictStrategy;

import com.example.inventoryapp.model.ItemMetadata;

/**
 * ItemMetadataDao
 * ---------------
 * Data Access Object (DAO) for ItemMetadata entities.
 * Provides CRUD operations for metadata linked to inventory items.
 * Best practice: use conflict strategies for inserts/updates and provide
 * delete operations by both object and foreign key.
 *
 * Notes:
 * - Metadata fields (description, location) are optional and may be null.
 * - Queries should handle null values gracefully.
 */
@Dao
public interface ItemMetadataDao {

    // --- Insert operations ---
    /**
     * Insert metadata for an item.
     * Uses REPLACE strategy to upsert metadata for the same item_id.
     * Optional fields (description, location) may be null.
     *
     * @param metadata ItemMetadata entity
     * @return row ID of inserted metadata
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertMetadata(ItemMetadata metadata);

    // --- Update operations ---
    /**
     * Update metadata for an item.
     * Optional fields may be null; update will persist null values.
     *
     * @param metadata ItemMetadata entity
     * @return number of rows affected
     */
    @Update
    int updateMetadata(ItemMetadata metadata);

    // --- Delete operations ---
    /**
     * Delete metadata by entity reference.
     *
     * @param metadata ItemMetadata entity
     * @return number of rows deleted
     */
    @Delete
    int deleteMetadata(ItemMetadata metadata);

    /**
     * Delete metadata by foreign key reference (item_id).
     *
     * @param itemId foreign key to Inventory
     * @return number of rows deleted
     */
    @Query("DELETE FROM item_metadata WHERE item_id = :itemId")
    int deleteByItemId(int itemId);

    // --- Retrieval operations ---
    /**
     * Retrieve metadata for a given item.
     * Returns null if no metadata exists (optional by design).
     *
     * @param itemId foreign key to Inventory
     * @return ItemMetadata or null
     */
    @Query("SELECT * FROM item_metadata WHERE item_id = :itemId LIMIT 1")
    ItemMetadata getMetadataForItem(int itemId);
}