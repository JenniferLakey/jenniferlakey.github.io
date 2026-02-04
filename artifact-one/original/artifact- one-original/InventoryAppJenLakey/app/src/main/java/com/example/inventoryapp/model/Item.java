package com.example.inventoryapp.model;

import java.io.Serializable;

/**
 * Item
 * ----
 * Plain DTO (Data Transfer Object) used for UI display in RecyclerView.
 * Not a Room entity. Represents a simplified view of Inventory + Metadata.
 *
 * Best practice:
 * - Keep immutable fields, provide constructor + getters.
 * - Avoid exposing mutable public setters.
 * - Metadata fields (description, location) are optional and may be null.
 */
public class Item implements Serializable {

    private final int id;
    private final String name;
    private final String category;
    private final String description; // optional, may be null
    private final String location;    // optional, may be null
    private final int quantity;

    // Full constructor for creating immutable Item objects
    public Item(int id, String name, String category, String description, String location, int quantity) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
        this.location = location;
        this.quantity = quantity;
    }

    // Optional empty constructor if frameworks require it (e.g., serialization)
    public Item() {
        this.id = 0;
        this.name = "";
        this.category = "";
        this.description = null; // explicitly null to represent "no metadata"
        this.location = null;    // explicitly null to represent "no metadata"
        this.quantity = 0;
    }

    // --- Getters ---
    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }

    /**
     * @return description or null if not provided
     */
    public String getDescription() { return description; }

    /**
     * @return location or null if not provided
     */
    public String getLocation() { return location; }

    public int getQuantity() { return quantity; }
}