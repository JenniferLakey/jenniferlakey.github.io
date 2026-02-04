package com.example.inventoryapp.utils;

import com.example.inventoryapp.model.Inventory;
import com.example.inventoryapp.model.Item;
import com.example.inventoryapp.model.ItemMetadata;
import com.example.inventoryapp.model.ItemWithMetadata;

/**
 * ItemMapper
 * ----------
 * Utility class for converting between Room entities (Inventory, ItemMetadata)
 * and UI-friendly POJOs (Item).
 * Best practice: always construct entities with their primary keys so Room can
 * persist updates correctly.
 */
public class ItemMapper {

    /**
     * Convert Inventory + Metadata relation to Item (UI-friendly DTO).
     *
     * @param relation Room relation object containing Inventory and ItemMetadata
     * @return Item DTO for UI binding
     */
    public static Item toItem(ItemWithMetadata relation) {
        ItemMetadata meta = relation.metadata;
        Inventory inv = relation.item;

        return new Item(
                inv.itemId,
                inv.itemName,
                inv.category,
                meta != null ? meta.description : null,
                meta != null ? meta.location : null,
                inv.quantity
        );
    }

    /**
     * Convert Item back to Inventory entity (for persistence).
     * Uses constructor with PK so Room can update the correct row.
     *
     * @param item UI-friendly Item DTO
     * @return Inventory entity
     */
    public static Inventory toInventory(Item item) {
        return new Inventory(
                item.getId(),              // PK
                item.getName(),
                item.getQuantity(),
                item.getCategory()
        );
    }

    /**
     * Convert Item back to ItemMetadata entity (for persistence).
     * Uses constructor with PK so Room can update the correct row.
     *
     * @param item UI-friendly Item DTO
     * @return ItemMetadata entity
     */
    public static ItemMetadata toMetadata(Item item) {
        return new ItemMetadata(
                item.getId(),              // FK to Inventory
                item.getDescription(),
                item.getLocation()
        );
    }
}