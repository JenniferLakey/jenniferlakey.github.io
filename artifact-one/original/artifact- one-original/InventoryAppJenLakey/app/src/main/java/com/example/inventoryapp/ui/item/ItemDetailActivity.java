package com.example.inventoryapp.ui.item;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.inventoryapp.R;
import com.example.inventoryapp.repository.InventoryRepository;
import com.example.inventoryapp.model.ItemWithMetadata;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

/**
 * ItemDetailActivity
 * ------------------
 * Displays details of a single inventory item.
 * Observes LiveData<ItemWithMetadata> so UI auto-refreshes when DB changes.
 * Allows user to adjust quantity, edit item, or delete item.
 * Persists changes via InventoryRepository and provides user feedback with Snackbars.
 */
public class ItemDetailActivity extends AppCompatActivity {

    // UI components
    private TextView tvItemName, tvCategory, tvDescription, tvLocation, tvQuantity;
    private ImageButton btnIncrease, btnDecrease, btnEdit, btnDelete;

    // Track current item state
    private int currentQuantity = 0;
    private int currentItemId;

    // Repository
    private InventoryRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        // --- Toolbar setup ---
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.app_name));
            getSupportActionBar().setSubtitle(getString(R.string.item_detail_page));
        }

        // --- Bind UI components ---
        tvItemName = findViewById(R.id.tvItemName);
        tvCategory = findViewById(R.id.tvCategory);
        tvDescription = findViewById(R.id.tvDescription);
        tvLocation = findViewById(R.id.tvLocation);
        tvQuantity = findViewById(R.id.tvQuantity);

        btnIncrease = findViewById(R.id.btnIncrease);
        btnDecrease = findViewById(R.id.btnDecrease);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);

        // --- Get item ID from Intent ---
        currentItemId = getIntent().getIntExtra("item_id", -1);

        // --- Repository setup ---
        repo = new InventoryRepository(getApplicationContext());

        // --- Observe item details ---
        repo.getItemWithMetadata(currentItemId).observe(this, item -> {
            if (item != null) {
                bindItemDetails(item);
            }
        });

        // --- Quantity increase ---
        btnIncrease.setOnClickListener(v -> adjustQty(currentItemId, currentQuantity + 1));

        // --- Quantity decrease ---
        btnDecrease.setOnClickListener(v -> {
            if (currentQuantity > 0) {
                adjustQty(currentItemId, currentQuantity - 1);
            }
        });

        // --- Edit item ---
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(ItemDetailActivity.this, EditItemActivity.class);
            intent.putExtra("item_id", currentItemId);
            intent.putExtra("item_name", tvItemName.getText().toString());
            intent.putExtra("category", tvCategory.getText().toString());
            intent.putExtra("description", tvDescription.getText().toString());
            intent.putExtra("location", tvLocation.getText().toString());
            intent.putExtra("quantity", currentQuantity);
            startActivity(intent);
        });

        // --- Delete item with Undo ---
        btnDelete.setOnClickListener(v -> {
            repo.getItemWithMetadata(currentItemId).observe(this, existing -> {
                if (existing != null) {
                    repo.deleteItemCascade(existing.item);
                    Snackbar.make(btnDelete, "Item deleted", Snackbar.LENGTH_LONG)
                            .setAction("Undo", undoView -> {
                                try {
                                    repo.insertItemWithMetadata(existing.item, existing.metadata);
                                    Snackbar.make(btnDelete, "Item restored", Snackbar.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    Snackbar.make(btnDelete, "Failed to restore item: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                                }
                            })
                            .show();
                    finish();
                } else {
                    Snackbar.make(btnDelete, "Failed to delete item", Snackbar.LENGTH_LONG).show();
                }
            });
        });
    }

    /**
     * Bind item details to UI components.
     */
    private void bindItemDetails(ItemWithMetadata item) {
        tvItemName.setText(item.item.itemName);

        // Null-safe fallbacks: show empty string if field is null
        tvCategory.setText(item.item.category != null ? item.item.category : "");
        tvDescription.setText(item.metadata != null && item.metadata.description != null ? item.metadata.description : "");
        tvLocation.setText(item.metadata != null && item.metadata.location != null ? item.metadata.location : "");

        currentQuantity = item.item.quantity;
        tvQuantity.setText(getString(R.string.quantity_label, currentQuantity));
    }


    /**
     * Adjust quantity and persist change in DB.
     * Shows Snackbar confirmation and triggers zero-stock notification if needed.
     */
    private void adjustQty(int itemId, int newQty) {
        try {
            repo.updateQuantity(itemId, newQty);
            currentQuantity = newQty;
            tvQuantity.setText(getString(R.string.quantity_label, currentQuantity));
            Snackbar.make(tvQuantity, "Quantity updated", Snackbar.LENGTH_SHORT).show();
        } catch (Exception e) {
            Snackbar.make(tvQuantity, "Failed to update quantity: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }
}