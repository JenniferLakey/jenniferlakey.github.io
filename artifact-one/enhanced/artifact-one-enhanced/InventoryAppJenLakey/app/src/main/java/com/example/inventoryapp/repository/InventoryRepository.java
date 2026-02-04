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
import android.content.SharedPreferences;
import com.example.inventoryapp.model.User;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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
 * - Centralizes quantity update logic so all UI paths share a single workflow.
 */
public class InventoryRepository {

    // Logging tag for consistent error reporting
    private static final String TAG = "InventoryRepository";

    private final InventoryDao inventoryDao;
    private final ItemMetadataDao metadataDao;
    private final Executor executor;

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

        executor = Executors.newSingleThreadExecutor();
    }

    // --- Zero-Stock Alert Helper (Centralized Notification/SMS Logic) ---

    /**
     * Sends the appropriate zero-stock alert (notification or SMS)
     * based on the logged-in user's stored phone number.
     *
     * Centralizing this logic ensures:
     * - No duplication across insert/update/quantity workflows
     * - Consistent behavior for all zero-stock events
     * - Clean, maintainable repository code
     */
    private void triggerZeroStockAlert(int itemId, String itemName) {

        // Retrieve logged-in user ID from SharedPreferences
        SharedPreferences prefs =
                context.getSharedPreferences("InventoryAppPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        String phone = null;

        if (userId != -1) {
            UserRepository userRepo = new UserRepository(context);
            User u = userRepo.getUserById(userId);
            phone = (u != null) ? u.phoneNumber : null;
        }

        if (phone != null && !phone.isEmpty()) {
            NotificationHelper.sendZeroStockAlert(context, itemId, itemName, phone);
        } else {
            NotificationHelper.sendZeroStock(context, itemId, itemName);
        }

        Log.w(TAG, "Zero stock alert sent for item ID: " + itemId);
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
            triggerZeroStockAlert((int) itemId, item.itemName);
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
    public void updateItemWithMetadata(Inventory item, ItemMetadata meta) {
        executor.execute(() -> {
            try {
                // --- Validation ---
                if (!ValidationUtils.isValidItemName(item.itemName)) {
                    Log.e(TAG, "Update failed: Invalid item name");
                    return;
                }
                if (!ValidationUtils.isValidQuantity(item.quantity)) {
                    Log.e(TAG, "Update failed: Invalid quantity");
                    return;
                }

                // Null-safe category validation: only validate if non-empty
                if (item.category != null && !item.category.trim().isEmpty()
                        && !ValidationUtils.isValidCategory(item.category)) {
                    Log.e(TAG, "Update failed: Invalid category");
                    return;
                }

                // --- Perform update ---
                int updated = inventoryDao.updateItem(item);
                int metaUpdated = 0;

        // Update metadata only if provided and at least one field is non-empty
                if (meta != null &&
                        ((meta.description != null && !meta.description.trim().isEmpty()) ||
                                (meta.location != null && !meta.location.trim().isEmpty()))) {

                    // Validate provided non-empty fields before update
                    boolean descValid = meta.description == null ||
                            meta.description.trim().isEmpty() ||
                            ValidationUtils.isValidMetadata(meta.description);

                    boolean locValid = meta.location == null ||
                            meta.location.trim().isEmpty() ||
                            ValidationUtils.isValidMetadata(meta.location);

                    if (!descValid || !locValid) {
                        Log.e(TAG, "Metadata validation failed, skipping metadata update");
                    } else {
                        metaUpdated = metadataDao.updateMetadata(meta);
                // If no existing row was updated, insert new metadata (upsert behavior)
                        if (metaUpdated == 0) {
                            metadataDao.insertMetadata(meta);
                            metaUpdated = 1;
                        }
                    }
                }// If meta is null or both fields empty, leave existing metadata as-is (optional by design)
                Log.i(TAG, "Item updated successfully, rows affected: "
                        + updated + " (inventory), " + metaUpdated + " (metadata)");


                // --- Trigger notification if stock is zero ---
                if (item.quantity == 0) {
                    triggerZeroStockAlert(item.itemId, item.itemName);
                }

            } catch (Exception e) {
                Log.e(TAG, "Error updating item with metadata", e);
            }
        });
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
    public void deleteItemById(int itemId) {
        executor.execute(() -> {
            int rows = inventoryDao.deleteById(itemId);
            metadataDao.deleteByItemId(itemId);
            Log.i(TAG, "Deleted itemId=" + itemId + " rows=" + rows);
        });
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
     * Synchronous lookup of an Inventory item by ID.
     * Used by ViewModel for delete-by-id workflows.
     */
    public Inventory getItemByIdSync(int itemId) {
        return inventoryDao.getItemByIdSync(itemId);
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

    // --- Quantity Update (Unified Workflow) ---

    /**
     * Unified quantity update workflow.
     *
     * This method centralizes all quantity changes so that every screen
     * (inventory list, detail, edit) follows the same validation rules,
     * persistence behavior, and zero-stock notification logic.
     *
     * Callers should pass a delta (positive or negative). The repository
     * will clamp the resulting quantity at zero and persist the change.
     *
     * @param itemId item primary key
     * @param delta  quantity change (e.g., +1 for increase, -1 for decrease)
     * @throws Exception if the item cannot be found or validation fails
     */
    public void updateItemQuantity(int itemId, int delta) {
        executor.execute(() -> {
            try {
                // --- Retrieve current item state ---
                Inventory current = inventoryDao.getItemByIdSync(itemId);
                if (current == null) {
                    Log.e(TAG, "Quantity update failed: Item not found for ID " + itemId);
                    return;
                }

                // --- Compute new quantity with clamping ---
                int newQty = current.quantity + delta;
                if (newQty < 0) newQty = 0;


                // --- Validation on resulting quantity ---
                if (!ValidationUtils.isValidQuantity(newQty)) {
                    Log.e(TAG, "Quantity update failed: Invalid resulting quantity " + newQty);
                    return;
                }


                // --- Persist update via DAO ---
                int rows = inventoryDao.updateQuantity(itemId, newQty);
                Log.i(TAG, "Unified quantity update for item ID: " + itemId
                        + " from " + current.quantity + " to " + newQty
                        + " (rows affected: " + rows + ")");


                // --- Trigger notification if stock is zero ---
                if (newQty == 0) {
                    triggerZeroStockAlert(itemId, current.itemName);
                }

            } catch (Exception e) {
                Log.e(TAG, "Error updating quantity", e);
            }
        });
    }

    /**
     * Legacy-style quantity update by absolute value.
     *
     * This method is retained for backward compatibility but internally
     * delegates to the unified {@link #updateItemQuantity(int, int)} workflow
     * by computing the delta from the current quantity.
     *
     * @param itemId item primary key
     * @param newQty new quantity value
     * @throws Exception if validation fails or item is missing
     */
    public void updateQuantity(int itemId, int newQty) throws Exception {
        // Retrieve current item to compute delta and reuse unified logic
        Inventory current = inventoryDao.getItemByIdSync(itemId);
        if (current == null) {
            Log.e(TAG, "Quantity update failed: Item not found for ID " + itemId);
            throw new Exception("Item not found.");
        }

        int delta = newQty - current.quantity;
        updateItemQuantity(itemId, delta);
    }
}