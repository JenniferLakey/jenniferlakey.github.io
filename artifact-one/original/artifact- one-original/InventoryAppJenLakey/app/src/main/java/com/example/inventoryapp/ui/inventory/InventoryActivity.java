package com.example.inventoryapp.ui.inventory;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventoryapp.R;
import com.example.inventoryapp.model.Item;
import com.example.inventoryapp.model.Inventory;
import com.example.inventoryapp.ui.item.AddItemActivity;
import com.example.inventoryapp.utils.SortFilterState;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * InventoryActivity
 * -----------------
 * Displays the inventory grid with search, filter, and sort functionality.
 * - Uses RecyclerView with InventoryAdapter to show items.
 * - Observes LiveData from InventoryViewModel so UI auto-refreshes when DB changes.
 * - Wires search bar and filter dropdown to update SortFilterState in ViewModel.
 */
public class InventoryActivity extends AppCompatActivity {

    // --- UI components ---
    private RecyclerView recyclerInventory;
    private SearchView searchView;
    private Spinner spinnerFilter;
    private FloatingActionButton fabAddItem;

    // --- Adapter ---
    private InventoryAdapter adapter;

    // --- Sort/filter state ---
    private SortFilterState sortFilterState;

    // --- ViewModel ---
    private InventoryViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        // --- Toolbar setup ---
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.app_name));
            getSupportActionBar().setSubtitle(getString(R.string.inventory_page));
        }

        // --- Bind UI components ---
        recyclerInventory = findViewById(R.id.recyclerInventory);
        searchView = findViewById(R.id.searchView);
        spinnerFilter = findViewById(R.id.spinnerFilter);
        fabAddItem = findViewById(R.id.fabAddItem);

        // --- Initialize SortFilterState ---
        sortFilterState = new SortFilterState();

        // --- RecyclerView setup ---
        recyclerInventory.setLayoutManager(new GridLayoutManager(this, 1));
        adapter = new InventoryAdapter(this, new ArrayList<>());
        recyclerInventory.setAdapter(adapter);

        // --- ViewModel setup ---
        viewModel = new ViewModelProvider(this).get(InventoryViewModel.class);

        // Observe LiveData from ViewModel
        viewModel.getItems().observe(this, inventories -> {
            // Convert and update adapter directly
            adapter.updateData(convertToItems(inventories));
        });

        // --- FAB: navigate to AddItemActivity ---
        fabAddItem.setOnClickListener(v ->
                startActivity(new Intent(InventoryActivity.this, AddItemActivity.class))
        );

        // --- Spinner setup for filter fields ---
        String[] filterOptions = {"Item Name", "Category", "Description", "Location"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, filterOptions);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(spinnerAdapter);

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                switch (filterOptions[position]) {
                    case "Item Name":
                        sortFilterState.setCurrentFilterField(SortFilterState.FilterField.ITEM_NAME);
                        break;
                    case "Category":
                        sortFilterState.setCurrentFilterField(SortFilterState.FilterField.CATEGORY);
                        break;
                    case "Description":
                        sortFilterState.setCurrentFilterField(SortFilterState.FilterField.DESCRIPTION);
                        break;
                    case "Location":
                        sortFilterState.setCurrentFilterField(SortFilterState.FilterField.LOCATION);
                        break;
                    default:
                        sortFilterState.clearFilter();
                }
                // Push updated state into ViewModel
                viewModel.setSortFilterState(sortFilterState);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sortFilterState.clearFilter();
                viewModel.setSortFilterState(sortFilterState);
            }
        });

        // --- SearchView setup for keyword filtering ---
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                sortFilterState.setCurrentFilterKeyword(query);
                viewModel.setSortFilterState(sortFilterState); // push to ViewModel
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                sortFilterState.setCurrentFilterKeyword(newText);
                viewModel.setSortFilterState(sortFilterState); // push to ViewModel
                return true;
            }
        });
    }

    /**
     * Convert Inventory entities to Item DTOs for adapter display.
     * Keeps UI layer decoupled from Room entities.
     */
    private List<Item> convertToItems(List<Inventory> inventories) {
        List<Item> items = new ArrayList<>();
        if (inventories == null) return items;

        for (Inventory inv : inventories) {
            items.add(new Item(
                    inv.itemId,
                    inv.itemName,
                    inv.category != null ? inv.category : "",
                    "", // description not shown in grid
                    "", // location not shown in grid
                    inv.quantity
            ));
        }
        return items;
    }
}