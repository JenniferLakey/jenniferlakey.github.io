package com.example.inventoryapp.ui.item;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.inventoryapp.R;
import com.example.inventoryapp.db.DbExecutor;
import com.example.inventoryapp.model.Inventory;
import com.example.inventoryapp.model.ItemMetadata;
import com.example.inventoryapp.repository.InventoryRepository;
import com.example.inventoryapp.utils.ValidationUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

/**
 * AddItemActivity
 * ----------------
 * UI screen for creating a new inventory item.
 * - Collects user input, validates it, and persists the item + metadata
 *   into the Room database via InventoryRepository.
 * - Finishes activity after successful insert so InventoryActivity observer auto-refreshes.
 */
public class AddItemActivity extends AppCompatActivity {

    // UI components
    private EditText etItemName, etCategory, etDescription, etLocation, etQuantity;
    private Button btnSaveItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        // --- Toolbar setup ---
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.app_name));
            getSupportActionBar().setSubtitle(getString(R.string.add_item_page));
        }

        // --- Bind UI components ---
        etItemName = findViewById(R.id.etItemName);
        etCategory = findViewById(R.id.etCategory);
        etDescription = findViewById(R.id.etDescription);
        etLocation = findViewById(R.id.etLocation);
        etQuantity = findViewById(R.id.etQuantity);
        btnSaveItem = findViewById(R.id.btnSaveItem);

        // --- Save button handler ---
        btnSaveItem.setOnClickListener(v -> {
            String name = etItemName.getText().toString().trim();
            String category = etCategory.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String location = etLocation.getText().toString().trim();
            int qty = parseIntSafe(etQuantity.getText().toString());

            // --- Basic validation before DB insert ---
            if (!ValidationUtils.isValidItemName(name)) {
                etItemName.setError("Item name is required (min 1 char)");
                return;
            }
            if (!ValidationUtils.isValidQuantity(qty)) {
                etQuantity.setError("Quantity must be non-negative");
                return;
            }

            // --- Persist item + metadata asynchronously ---
            DbExecutor.IO.execute(() -> {
                InventoryRepository repo = new InventoryRepository(getApplicationContext());
                try {
                    // Use the convenience constructor for Inventory
                    Inventory inv = new Inventory(name, qty, category.isEmpty() ? null : category);

                    // Create metadata object (PK auto-generated, itemId set after insert)
                    ItemMetadata meta = new ItemMetadata(inv.itemId, description, location);

                    repo.insertItemWithMetadata(inv, meta);

                    // Show confirmation Snackbar and close activity
                    runOnUiThread(() -> {
                        Snackbar.make(btnSaveItem, "Item added successfully", Snackbar.LENGTH_LONG).show();
                        finish(); // InventoryActivity observer will auto-refresh
                    });
                } catch (Exception ex) {
                    // Show error Snackbar (e.g., duplicate name)
                    runOnUiThread(() ->
                            Snackbar.make(btnSaveItem, "Failed to add item: " + ex.getMessage(), Snackbar.LENGTH_LONG).show()
                    );
                }
            });
        });
    }

    /**
     * Safely parse integer from string.
     * Returns 0 if parsing fails.
     */
    private int parseIntSafe(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }
}