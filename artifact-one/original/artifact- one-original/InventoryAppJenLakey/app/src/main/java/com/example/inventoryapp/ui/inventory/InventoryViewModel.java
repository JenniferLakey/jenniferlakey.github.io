package com.example.inventoryapp.ui.inventory;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.inventoryapp.model.Inventory;
import com.example.inventoryapp.repository.InventoryRepository;
import com.example.inventoryapp.utils.SortFilterState;

import java.util.List;

/**
 * InventoryViewModel
 * ------------------
 * Acts as the bridge between UI and Repository.
 * - Exposes LiveData of inventory items to the UI.
 * - Applies SortFilterState for filtering/sorting.
 * - Survives configuration changes (AndroidViewModel).
 * - Ensures only one active LiveData source at a time to avoid stale data.
 */
public class InventoryViewModel extends AndroidViewModel {

    // Repository layer for database access
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
}