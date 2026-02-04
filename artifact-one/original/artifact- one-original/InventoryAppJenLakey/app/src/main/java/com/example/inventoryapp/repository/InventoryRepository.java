package com.example.inventoryapp.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.example.inventoryapp.db.AppDatabase;
import com.example.inventoryapp.dao.InventoryDao;
import com.example.inventoryapp.dao.ItemMetadataDao;
import com.example.inventoryapp.model.Inventory;
import com.example.inventoryapp.model.ItemMetadata;
import com.example.inventoryapp.model.ItemWithMetadata;
import com.example.inventoryapp.notifications.NotificationHelper;
import com.example.inventoryapp.utils.SortFilterState;
import com.example.inventoryapp.utils.ValidationUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * InventoryRepository
 * -------------------
 * Repository layer that wraps DAO operations for Inventory and ItemMetadata.
 *
 * Responsibilities:
 * - Provides business logic such as enforcing unique item names, cascading deletes,
 *   and dynamic sort/filter integration.
 * - Ensures updates are tied to primary key (itemId) so Room persists changes.
 * - Exposes LiveData for retrieval so UI auto-refreshes when DB changes.
 * - Triggers notifications when items reach zero stock.
 * - Treats metadata (description, location) as optional.
 */
public class InventoryRepository {

    // Logging tag for consistent error reporting
    private static final String TAG = "InventoryRepository";

    private final InventoryDao inventoryDao;
    private final ItemMetadataDao metadataDao;
    private final Context context; // needed for notifications

    /**
     * Constructor initializes DAO references from AppDatabase singleton.
     *
     * @param ctx Application context
     */
    public InventoryRepository(Context ctx) {
        AppDatabase db = AppDatabase.getInstance(ctx);
        inventoryDao = db.inventoryDao();
        metadataDao = db.itemMetadataDao();
        context = ctx.getApplicationContext();
    }

    // --- Insert ---
    /**
     * Inserts a new Inventory item with optional metadata.
     * Performs validation and enforces unique item names.
     *
     * @param item Inventory entity
     * @param meta ItemMetadata entity (may be null or have empty fields)
     * @return newly inserted itemId
     * @throws Exception if validation fails
     */
    public long insertItemWithMetadata(Inventory item, ItemMetadata meta) throws Exception {
        // --- Validation ---
        if (!ValidationUtils.isValidItemName(item.itemName)) {
            Log.e(TAG, "Insert failed: Invalid item name");
            throw new Exception("Invalid item name. Cannot be empty.");
        }
        if (!ValidationUtils.isValidQuantity(item.quantity)) {
            Log.e(TAG, "Insert failed: Invalid quantity");
            throw new Exception("Invalid quantity. Must be non-negative.");
        }
        if (item.category != null && !item.category.trim().isEmpty() &&
                !ValidationUtils.isValidCategory(item.category)) {
            Log.e(TAG, "Insert failed: Invalid category");
            throw new Exception("Invalid category.");
        }
        if (inventoryDao.countByName(item.itemName) > 0) {
            Log.e(TAG, "Insert failed: Duplicate item name");
            throw new Exception("Item name must be unique.");
        }

        // --- Perform insert ---
        long itemId = inventoryDao.insertItem(item);

        // Insert metadata only if provided and at least one field is non-empty
        if (meta != null &&
                ((meta.description != null && !meta.description.trim().isEmpty()) ||
                        (meta.location != null && !meta.location.trim().isEmpty()))) {

            // If provided, validate the provided non-empty fields (optional by design)
            if (meta.description != null && !meta.description.trim().isEmpty() &&
                    !ValidationUtils.isValidMetadata(meta.description)) {
                Log.e(TAG, "Insert warning: Description failed validation, skipping metadata insert");
            } else if (meta.location != null && !meta.location.trim().isEmpty() &&
                    !ValidationUtils.isValidMetadata(meta.location)) {
                Log.e(TAG, "Insert warning: Location failed validation, skipping metadata insert");
            } else {
                meta.itemId = (int) itemId;
                metadataDao.insertMetadata(meta);
                Log.i(TAG, "Metadata inserted for item ID: " + itemId);
            }
        } else {
            Log.i(TAG, "No metadata provided for item ID: " + itemId);
        }

        Log.i(TAG, "Item inserted successfully with ID: " + itemId);

        // --- Trigger notification if stock is zero ---
        if (item.quantity == 0) {
            NotificationHelper.sendZeroStock(context, (int) itemId, item.itemName);
            Log.w(TAG, "Zero stock notification sent for new item ID: " + itemId);
        }

        return itemId;
    }

    // --- Update ---
    /**
     * Updates an existing Inventory item and its optional metadata.
     * Validates fields and ensures Room matches by primary key.
     *
     * @param item Inventory entity
     * @param meta ItemMetadata entity (may be null or have empty fields)
     * @return number of rows affected
     * @throws Exception if validation fails
     */
    public int updateItemWithMetadata(Inventory item, ItemMetadata meta) throws Exception {
        // --- Validation ---
        if (!ValidationUtils.isValidItemName(item.itemName)) {
            Log.e(TAG, "Update failed: Invalid item name");
            throw new Exception("Invalid item name.");
        }
        if (!ValidationUtils.isValidQuantity(item.quantity)) {
            Log.e(TAG, "Update failed: Invalid quantity");
            throw new Exception("Invalid quantity.");
        }
        // Null-safe category validation: only validate if non-empty
        if (item.category != null && !item.category.trim().isEmpty() &&
                !ValidationUtils.isValidCategory(item.category)) {
            Log.e(TAG, "Update failed: Invalid category");
            throw new Exception("Invalid category.");
        }

        // --- Perform update ---
        int updated = inventoryDao.updateItem(item);
        int metaUpdated = 0;

        // Update metadata only if provided and at least one field is non-empty
        if (meta != null &&
                ((meta.description != null && !meta.description.trim().isEmpty()) ||
                        (meta.location != null && !meta.location.trim().isEmpty()))) {

            // Validate provided non-empty fields before update
            if (meta.description != null && !meta.description.trim().isEmpty() &&
                    !ValidationUtils.isValidMetadata(meta.description)) {
                Log.e(TAG, "Update warning: Description failed validation, skipping metadata update");
            } else if (meta.location != null && !meta.location.trim().isEmpty() &&
                    !ValidationUtils.isValidMetadata(meta.location)) {
                Log.e(TAG, "Update warning: Location failed validation, skipping metadata update");
            } else {
                metaUpdated = metadataDao.updateMetadata(meta);
                // If no existing row was updated, insert new metadata (upsert behavior)
                if (metaUpdated == 0) {
                    metadataDao.insertMetadata(meta);
                    metaUpdated = 1;
                }
            }
        } // If meta is null or both fields empty, leave existing metadata as-is (optional by design)

        if (updated > 0 || metaUpdated > 0) {
            Log.i(TAG, "Item updated successfully, rows affected: "
                    + updated + " (inventory), " + metaUpdated + " (metadata)");
        } else {
            Log.w(TAG, "Update executed but no rows affected for item ID: " + item.itemId);
        }

        // --- Trigger notification if stock is zero ---
        if (item.quantity == 0) {
            NotificationHelper.sendZeroStock(context, item.itemId, item.itemName);
            Log.w(TAG, "Zero stock notification sent for updated item ID: " + item.itemId);
        }

        return updated + metaUpdated;
    }

    // --- Delete ---
    /**
     * Deletes an Inventory item and cascades to metadata.
     *
     * @param item Inventory entity
     * @return number of rows deleted
     */
    public int deleteItemCascade(Inventory item) {
        int deleted = inventoryDao.deleteItem(item);
        Log.i(TAG, "Item deleted successfully, rows affected: " + deleted);
        return deleted;
    }

    // --- Retrieval ---
    /**
     * Retrieves an Inventory item with its metadata as LiveData.
     *
     * @param itemId item primary key
     * @return LiveData of ItemWithMetadata
     */
    public LiveData<ItemWithMetadata> getItemWithMetadata(int itemId) {
        return inventoryDao.getItemWithMetadata(itemId);
    }

    /**
     * Dynamic query based on SortFilterState.
     * Always LEFT JOINs metadata so items without metadata are included.
     * Filters include rows where metadata matches OR metadata is missing (NULL).
     * Returns LiveData so UI auto-refreshes when DB changes.
     *
     * @param state current sort/filter state
     * @return LiveData list of Inventory items
     */
    public LiveData<List<Inventory>> getItems(SortFilterState state) {
        StringBuilder sql = new StringBuilder(
                "SELECT i.* FROM inventory i LEFT JOIN item_metadata m ON i.item_id = m.item_id ");
        List<Object> argsList = new ArrayList<>();

        // Add filter if active
        if (state.hasFilter()) {
            sql.append("WHERE ");
            switch (state.getCurrentFilterField()) {
                case ITEM_NAME:
                    sql.append("i.item_name LIKE '%' || ? || '%' COLLATE NOCASE");
                    argsList.add(state.getCurrentFilterKeyword());
                    break;
                case CATEGORY:
                    sql.append("i.category LIKE '%' || ? || '%' COLLATE NOCASE");
                    argsList.add(state.getCurrentFilterKeyword());
                    break;
                case DESCRIPTION:
                    sql.append("(m.description LIKE '%' || ? || '%' COLLATE NOCASE OR m.description IS NULL)");
                    argsList.add(state.getCurrentFilterKeyword());
                    break;
                case LOCATION:
                    sql.append("(m.location LIKE '%' || ? || '%' COLLATE NOCASE OR m.location IS NULL)");
                    argsList.add(state.getCurrentFilterKeyword());
                    break;
                default:
                    break;
            }
        }

        // Add sort
        sql.append(" ORDER BY ");
        switch (state.getCurrentSort()) {
            case NAME_ASC:
                sql.append("i.item_name ASC");
                break;
            case QUANTITY_ASC:
                sql.append("i.quantity ASC");
                break;
            case QUANTITY_DESC:
                sql.append("i.quantity DESC");
                break;
            case CATEGORY_ASC:
                sql.append("i.category ASC");
                break;
        }

        // Always pass a non-null array
        Object[] args = argsList.toArray(new Object[0]);

        return inventoryDao.filterWithRaw(new SimpleSQLiteQuery(sql.toString(), args));
    }

    // --- Quantity Update ---
    /**
     * Updates item quantity and triggers zero-stock notification if needed.
     *
     * @param itemId item primary key
     * @param newQty new quantity value
     * @throws Exception if validation fails
     */
    public void updateQuantity(int itemId, int newQty) throws Exception {
        // --- Validation ---
        if (!ValidationUtils.isValidQuantity(newQty)) {
            Log.e(TAG, "Quantity update failed: Invalid quantity");
            throw new Exception("Invalid quantity.");
        }

        // --- Perform update ---
        int rows = inventoryDao.updateQuantity(itemId, newQty);
        Log.i(TAG, "Quantity updated for item ID: " + itemId
                + " to " + newQty + " (rows affected: " + rows + ")");

        // --- Trigger notification if stock is zero ---
        if (newQty == 0) {
            Inventory item = inventoryDao.getItemByIdSync(itemId);
            if (item != null) {
                NotificationHelper.sendZeroStock(context, itemId, item.itemName);
                Log.w(TAG, "Zero stock notification sent for item ID: " + itemId);
            }
        }
    }
}