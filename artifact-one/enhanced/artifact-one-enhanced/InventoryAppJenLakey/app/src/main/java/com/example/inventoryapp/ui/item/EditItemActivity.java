package com.example.inventoryapp.ui.item;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.inventoryapp.R;
import com.example.inventoryapp.model.Inventory;
import com.example.inventoryapp.model.ItemMetadata;
import com.example.inventoryapp.model.ItemWithMetadata;
import com.example.inventoryapp.ui.inventory.InventoryViewModel;
import com.example.inventoryapp.utils.ValidationUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

/**
 * EditItemActivity
 * ----------------
 * UI screen for editing an existing inventory item.
 *
 * Responsibilities:
 * - Observes LiveData<ItemWithMetadata> so fields auto-refresh when DB changes.
 * - Allows user to update name, category, description, location, and quantity.
 * - Delegates all persistence operations to InventoryViewModel.
 * - Quantity changes are routed through the unified quantity update workflow.
 */
public class EditItemActivity extends AppCompatActivity {

    private static final String TAG = "EditItemActivity";

    // UI components
    private EditText etItemName, etCategory, etDescription, etLocation, etQuantity;
    private Button btnSaveChanges;

    // Track the item being edited
    private int currentItemId;
    private int currentMetadataId;
    private int originalQuantity;

    // ViewModel reference
    private InventoryViewModel viewModel;

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

        // --- ViewModel setup ---
        viewModel = new ViewModelProvider(this).get(InventoryViewModel.class);

        // --- Get item ID from Intent ---
        currentItemId = getIntent().getIntExtra("item_id", -1);

        // --- Observe LiveData<ItemWithMetadata> for auto-refresh ---
        viewModel.getItem(currentItemId).observe(this, existing -> {
            if (existing != null && existing.item != null) {
                populateFields(existing);
            }
        });

        // --- Save changes handler ---
        btnSaveChanges.setOnClickListener(v -> saveChanges());
    }

    /**
     * Populate UI fields with the latest DB state.
     */
    private void populateFields(ItemWithMetadata existing) {
        etItemName.setText(existing.item.itemName);
        etCategory.setText(existing.item.category != null ? existing.item.category : "");
        etQuantity.setText(String.valueOf(existing.item.quantity));

        originalQuantity = existing.item.quantity;

        if (existing.metadata != null) {
            currentMetadataId = existing.metadata.metadataId;
            etDescription.setText(existing.metadata.description != null ? existing.metadata.description : "");
            etLocation.setText(existing.metadata.location != null ? existing.metadata.location : "");
        } else {
            currentMetadataId = -1;
            etDescription.setText("");
            etLocation.setText("");
        }
    }

    /**
     * Validate input fields and persist changes through the ViewModel.
     * Quantity changes are routed through the unified quantity workflow.
     */
    private void saveChanges() {
        String name = etItemName.getText().toString().trim();
        String category = etCategory.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        int newQty = parseIntSafe(etQuantity.getText().toString());

        // --- Basic validation ---
        if (!ValidationUtils.isValidItemName(name)) {
            etItemName.setError("Item name is required");
            return;
        }
        if (!ValidationUtils.isValidQuantity(newQty)) {
            etQuantity.setError("Quantity must be non-negative");
            return;
        }

        // --- Compute quantity delta for unified workflow ---
        int delta = newQty - originalQuantity;

        // --- Update quantity if it changed ---
        if (delta != 0) {
            viewModel.updateQuantity(currentItemId, delta);
        }

        // --- Construct updated Inventory entity (quantity preserved via unified workflow) ---
        Inventory updated = new Inventory(
                currentItemId,
                name,
                newQty,
                category.isEmpty() ? null : category
        );

        // --- Construct updated metadata entity ---
        ItemMetadata meta = null;
        if (!description.isEmpty() || !location.isEmpty()) {
            meta = new ItemMetadata(
                    currentMetadataId > 0 ? currentMetadataId : 0,  // let Room assign ID if inserting
                    currentItemId,
                    description.isEmpty() ? null : description,
                    location.isEmpty() ? null : location
            );
        }

        try {
            // Use ViewModel to persist metadata/name/category updates
            viewModel.updateItem(updated, meta);
            Snackbar.make(btnSaveChanges, "Item updated successfully", Snackbar.LENGTH_LONG).show();
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Failed to update item", e);
            Snackbar.make(btnSaveChanges, "Failed to update item. Please try again.", Snackbar.LENGTH_LONG).show();
        }
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