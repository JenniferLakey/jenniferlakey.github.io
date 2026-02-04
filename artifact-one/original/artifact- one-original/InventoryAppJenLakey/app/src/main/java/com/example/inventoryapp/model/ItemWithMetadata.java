package com.example.inventoryapp.model;

import androidx.annotation.Nullable;
import androidx.room.Embedded;
import androidx.room.Relation;

/**
 * ItemWithMetadata
 * ----------------
 * Room relation class that represents an Inventory entity together with its
 * associated ItemMetadata. Useful for queries that need both core item data
 * and optional metadata in a single object.
 *
 * Notes:
 * - This is not an @Entity, but a POJO used by Room to map relationships.
 * - Metadata is optional: relation may return null if no metadata exists.
 */
public class ItemWithMetadata {

    // Embedded Inventory entity (⚠ not the Item DTO, but the Room entity)
    @Embedded
    public Inventory item;

    // Related metadata entity (nullable: not all items have metadata)
    @Relation(
            parentColumn = "item_id",   // column in Inventory
            entityColumn = "item_id"    // column in ItemMetadata
    )
    @Nullable
    public ItemMetadata metadata;
}