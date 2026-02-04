package com.example.inventoryapp.ui.item;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import com.example.inventoryapp.R;
import com.example.inventoryapp.db.DbExecutor;
import com.example.inventoryapp.model.Inventory;
import com.example.inventoryapp.model.ItemMetadata;
import com.example.inventoryapp.model.ItemWithMetadata;
import com.example.inventoryapp.repository.InventoryRepository;
import com.example.inventoryapp.utils.ValidationUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

/**
 * EditItemActivity
 * ----------------
 * UI screen for editing an existing inventory item.
 * - Observes LiveData<ItemWithMetadata> so fields auto-refresh if DB changes.
 * - Allows user to update values and persists changes via InventoryRepository.
 * - Finishes activity after successful update so InventoryActivity observer auto-refreshes.
 */
public class EditItemActivity extends AppCompatActivity {

    private static final String TAG = "EditItemActivity";

    // UI components
    private EditText etItemName, etCategory, etDescription, etLocation, etQuantity;
    private Button btnSaveChanges;

    // Track the item being edited
    private int currentItemId;
    private int currentMetadataId; // track metadata PK for updates

    // Repository reference
    private InventoryRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        // --- Toolbar setup ---
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.app_name));
            getSupportActionBar().setSubtitle(getString(R.string.edit_item_page));
        }

        // --- Bind UI components ---
        etItemName = findViewById(R.id.etItemName);
        etCategory = findViewById(R.id.etCategory);
        etDescription = findViewById(R.id.etDescription);
        etLocation = findViewById(R.id.etLocation);
        etQuantity = findViewById(R.id.etQuantity);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);

        // --- Initialize repository ---
        repo = new InventoryRepository(getApplicationContext());

        // --- Get item ID from Intent ---
        currentItemId = getIntent().getIntExtra("item_id", -1);

        // --- Observe LiveData<ItemWithMetadata> for auto-refresh ---
        LiveData<ItemWithMetadata> liveData = repo.getItemWithMetadata(currentItemId);
        liveData.observe(this, existing -> {
            if (existing != null && existing.item != null) {
                // Populate fields whenever DB changes
                etItemName.setText(existing.item.itemName);
                etCategory.setText(existing.item.category != null ? existing.item.category : "");
                etQuantity.setText(String.valueOf(existing.item.quantity));

                if (existing.metadata != null) {
                    currentMetadataId = existing.metadata.metadataId; // capture PK for updates
                    etDescription.setText(existing.metadata.description != null ? existing.metadata.description : "");
                    etLocation.setText(existing.metadata.location != null ? existing.metadata.location : "");
                } else {
                    currentMetadataId = 0;
                    etDescription.setText("");
                    etLocation.setText("");
                }
            }
        });

        // --- Save changes handler ---
        btnSaveChanges.setOnClickListener(v -> {
            String name = etItemName.getText().toString().trim();
            String category = etCategory.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String location = etLocation.getText().toString().trim();
            int qty = parseIntSafe(etQuantity.getText().toString());

            // --- Basic validation ---
            if (!ValidationUtils.isValidItemName(name)) {
                etItemName.setError("Item name is required");
                return;
            }
            if (!ValidationUtils.isValidQuantity(qty)) {
                etQuantity.setError("Quantity must be non-negative");
                return;
            }

            // --- Persist changes asynchronously ---
            DbExecutor.IO.execute(() -> {
                try {
                    // Construct updated Inventory entity with PK
                    Inventory updated = new Inventory(
                            currentItemId,
                            name,
                            qty,
                            category.isEmpty() ? null : category // null if blank
                    );

                    // Construct updated ItemMetadata entity with PK
                    ItemMetadata meta = new ItemMetadata(
                            currentMetadataId,
                            currentItemId,
                            description.isEmpty() ? null : description, // null if blank
                            location.isEmpty() ? null : location       // null if blank
                    );

                    // Commit changes
                    int rows = repo.updateItemWithMetadata(updated, meta);

                    runOnUiThread(() -> {
                        if (rows > 0) {
                            Snackbar.make(btnSaveChanges, "Item updated successfully", Snackbar.LENGTH_SHORT).show();
                            finish(); // InventoryActivity observer will auto-refresh
                        } else {
                            Snackbar.make(btnSaveChanges, "No changes were saved.", Snackbar.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "Failed to update item", e);
                    runOnUiThread(() -> Snackbar.make(
                            btnSaveChanges,
                            "Failed to update item. Please try again.",
                            Snackbar.LENGTH_LONG
                    ).show());
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