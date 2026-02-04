package com.example.inventoryapp.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

import static androidx.room.ForeignKey.CASCADE;

/**
 * ItemMetadata
 * ------------
 * Entity representing additional metadata for an inventory item.
 * - Linked to Inventory via a foreign key (item_id).
 * - Uses metadataId as its own primary key for uniqueness.
 * - Enforces referential integrity with CASCADE so metadata is deleted when parent Inventory is deleted.
 * - Description and location are optional fields (nullable).
 */
@Entity(
        tableName = "item_metadata",
        foreignKeys = @ForeignKey(
                entity = Inventory.class,
                parentColumns = "item_id",
                childColumns = "item_id",
                onDelete = CASCADE // delete metadata when parent Inventory is deleted
        ),
        indices = {@Index("item_id")} // index for faster joins/queries
)
public class ItemMetadata {

    // --- Primary key ---
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "metadata_id")
    public int metadataId;

    // --- Foreign key reference to Inventory (required) ---
    @NonNull
    @ColumnInfo(name = "item_id")
    public int itemId;

    // --- Optional fields ---
    @ColumnInfo(name = "description", defaultValue = "NULL")
    public String description; // may be null if not provided

    @ColumnInfo(name = "location", defaultValue = "NULL")
    public String location;    // may be null if not provided

    // --- Constructor used by Room (includes PK) ---
    public ItemMetadata(int metadataId, int itemId, String description, String location) {
        this.metadataId = metadataId;
        this.itemId = itemId;
        this.description = description;
        this.location = location;
    }

    // --- Convenience constructor for inserts (ignored by Room) ---
    @Ignore
    public ItemMetadata(int itemId, String description, String location) {
        this.itemId = itemId;
        this.description = description;
        this.location = location;
    }
}