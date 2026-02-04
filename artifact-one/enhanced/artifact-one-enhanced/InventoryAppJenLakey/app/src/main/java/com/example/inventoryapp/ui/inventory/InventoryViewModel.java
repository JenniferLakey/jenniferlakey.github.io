package com.example.inventoryapp.ui.inventory;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.inventoryapp.model.Inventory;
import com.example.inventoryapp.model.ItemMetadata;
import com.example.inventoryapp.model.ItemWithMetadata;
import com.example.inventoryapp.repository.InventoryRepository;
import com.example.inventoryapp.utils.SortFilterState;

import java.util.List;

/**
 * InventoryViewModel
 * ------------------
 * Acts as the bridge between UI and Repository.
 *
 * Responsibilities:
 * - Exposes LiveData of inventory items to the UI.
 * - Applies SortFilterState for filtering/sorting.
 * - Survives configuration changes (AndroidViewModel).
 * - Ensures only one active LiveData source at a time to avoid stale data.
 * - Provides unified, lifecycle-aware entry points for item retrieval,
 *   quantity updates, deletion, and restoration.
 */
public class InventoryViewModel extends AndroidViewModel {

    // Repository layer for database access and business logic
    private final InventoryRepository repository;

    // MediatorLiveData allows swapping sources dynamically
    private final MediatorLiveData<List<Inventory>> itemsLiveData = new MediatorLiveData<>();

    // Current filter/sort state
    private SortFilterState sortFilterState = new SortFilterState();

    // Track the currently active LiveData source so we can remove it before adding a new one
    private LiveData<List<Inventory>> currentSource;

    /**
     * Constructor
     * Initializes repository and loads initial data.
     */
    public InventoryViewModel(@NonNull Application application) {
        super(application);
        repository = new InventoryRepository(application);
        refresh(); // load initial state
    }

    /**
     * Expose inventory items to UI as LiveData.
     * Observed by Fragments/Activities to auto-update the adapter.
     */
    public LiveData<List<Inventory>> getItems() {
        return itemsLiveData;
    }

    /**
     * Update the current sort/filter state.
     * Triggers a refresh to rebuild the query with new parameters.
     */
    public void setSortFilterState(SortFilterState state) {
        this.sortFilterState = state;
        refresh();
    }

    /**
     * Refresh the LiveData source.
     * - Removes any previous source to prevent multiple observers.
     * - Adds the new source from repository based on current SortFilterState.
     */
    public void refresh() {
        // Remove old source if present
        if (currentSource != null) {
            itemsLiveData.removeSource(currentSource);
        }

        // Build new query source
        currentSource = repository.getItems(sortFilterState);

        // Attach new source
        itemsLiveData.addSource(currentSource, itemsLiveData::setValue);
    }


    // --- Unified Quantity Update Workflow ---

    /**
     * Unified quantity update entry point for all UI layers.
     *
     * Activities and adapters should call this method instead of interacting
     * with the repository or DAO directly. This ensures:
     * - Consistent validation rules
     * - Lifecycle-safe updates
     * - Automatic LiveData refresh
     * - No duplicated logic across screens
     *
     * @param itemId item primary key
     * @param delta  quantity change (+1 increase, -1 decrease)
     */
    public void updateQuantity(int itemId, int delta) {
        try {
            repository.updateItemQuantity(itemId, delta);
            refresh(); // ensure UI reflects updated DB state
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // --- Full Item Update Workflow (Name, Metadata) ---

    /**
     * Update an existing item and its metadata.
     *
     * This method is used by EditItemActivity when the user edits fields such as
     * name, category, description, or location. Quantity changes are handled
     * separately through the unified quantity workflow.
     *
     * This ensures:
     * - All full-item edits flow through the repository's update workflow
     * - Metadata upsert logic is respected
     * - Zero-stock alerts remain centralized in the repository
     *
     * @param item Inventory entity containing updated core fields
     * @param meta ItemMetadata entity containing updated description/location
     */
    public void updateItem(Inventory item, ItemMetadata meta) {
        try {
            repository.updateItemWithMetadata(item, meta);
            refresh(); // ensure UI reflects updated DB state
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // --- Item Retrieval for Detail Screens ---

    /**
     * Retrieve a single item with its metadata.
     *
     * This is used by ItemDetailActivity and EditItemActivity to observe
     * the latest state of a specific item.
     *
     * @param itemId primary key of the item
     * @return LiveData<ItemWithMetadata> that updates automatically
     */
    public LiveData<ItemWithMetadata> getItem(int itemId) {
        return repository.getItemWithMetadata(itemId);
    }


    // --- Delete + Undo Restore Support ---

    /**
     * Delete an item and cascade to metadata.
     * <p>
     * UI components should call this instead of accessing the repository directly.
     *
     * @param item Inventory entity to delete
     */
    public void deleteItem(Inventory item) {
        repository.deleteItemCascade(item);
        refresh();
    }

    /**
     * Delete an item by its ID.
     * ViewModel resolves the entity and delegates to the existing delete workflow.
     *
     * @param itemId primary key of the item to delete
     */
    public void deleteItemById(int itemId) {
        repository.deleteItemById(itemId);
        refresh();
    }

    /**
     * Restore an item and its metadata (used for Undo actions).
     *
     * @param item Inventory entity
     * @param meta ItemMetadata entity (may be null)
     * @throws Exception if validation fails
     */
    public void restoreItem(Inventory item, ItemMetadata meta) throws Exception {
        repository.insertItemWithMetadata(item, meta);
        refresh();
    }
}