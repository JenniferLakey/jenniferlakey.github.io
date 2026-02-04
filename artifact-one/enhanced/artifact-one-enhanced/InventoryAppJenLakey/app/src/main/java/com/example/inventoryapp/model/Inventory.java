package com.example.inventoryapp.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

/**
 * Inventory
 * ---------
 * Entity representing an inventory item in the database.
 * - Primary key: itemId (auto-generated).
 * - Enforces unique item names via index.
 * - Includes item name, quantity, and category.
 * Best practice: enforce non-null constraints where required,
 * provide clear column names, and document nullable fields.
 */
@Entity(
        tableName = "inventory",
        indices = {@Index(value = {"item_name"}, unique = true)} // enforce unique item names
)
public class Inventory {

    // --- Primary key ---
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "item_id")
    public int itemId;

    // --- Item name (unique, required) ---
    @NonNull
    @ColumnInfo(name = "item_name")
    public String itemName;

    // --- Quantity (required, defaults to 0) ---
    @ColumnInfo(name = "quantity", defaultValue = "0")
    public int quantity;

    // --- Category (nullable, optional) ---
    @ColumnInfo(name = "category")
    public String category; // may be null if not specified

    // --- Default constructor (used by Room) ---
    public Inventory(int itemId, @NonNull String itemName, int quantity, String category) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.category = category;
    }

    // --- Convenience constructor for inserts (ignored by Room) ---
    @Ignore
    public Inventory(@NonNull String itemName, int quantity, String category) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.category = category;
    }

}