package com.example.inventoryapp.ui.item;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.inventoryapp.R;
import com.example.inventoryapp.model.ItemWithMetadata;
import com.example.inventoryapp.ui.inventory.InventoryViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

/**
 * ItemDetailActivity
 * ------------------
 * Displays details of a single inventory item.
 * Observes LiveData<ItemWithMetadata> so UI auto-refreshes when DB changes.
 * Delegates all quantity updates, deletion, and restoration to the ViewModel
 * to ensure lifecycle-safe, unified, and consistent behavior across the app.
 */
public class ItemDetailActivity extends AppCompatActivity {

    // UI components
    private TextView tvItemName, tvCategory, tvDescription, tvLocation, tvQuantity;
    private ImageButton btnIncrease, btnDecrease, btnEdit, btnDelete, btnBack;

    // Track current item state
    private int currentQuantity = 0;
    private int currentItemId;
    private ItemWithMetadata lastKnownItem;

    // ViewModel (replaces direct repository access)
    private InventoryViewModel viewModel;

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
        btnBack = findViewById(R.id.btnBack);

        // --- Get item ID from Intent ---
        currentItemId = getIntent().getIntExtra("item_id", -1);

        // --- ViewModel setup ---
        viewModel = new ViewModelProvider(this).get(InventoryViewModel.class);

        // --- Observe item details ---
        viewModel.getItem(currentItemId).observe(this, item -> {
            if (item != null) {
                lastKnownItem = item;   // capture the latest state
                bindItemDetails(item);
            }
        });

        // --- Quantity increase ---
        btnIncrease.setOnClickListener(v -> {
            viewModel.updateQuantity(currentItemId, +1);
            Snackbar.make(tvQuantity, "Quantity increased", Snackbar.LENGTH_LONG).show();
        });

        // --- Quantity decrease ---
        btnDecrease.setOnClickListener(v -> {
            if (currentQuantity > 0) {
                viewModel.updateQuantity(currentItemId, -1);
                Snackbar.make(tvQuantity, "Quantity decreased", Snackbar.LENGTH_LONG).show();
            }
        });

        // --- Edit item ---
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(ItemDetailActivity.this, EditItemActivity.class);
            // Only pass the ID. EditItemActivity will observe LiveData.
            intent.putExtra("item_id", currentItemId);
            startActivity(intent);
        });

        // --- Delete item with Undo ---
        btnDelete.setOnClickListener(v -> {
            viewModel.deleteItemById(currentItemId);

            Snackbar.make(btnDelete, "Item deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo", undoView -> {
                        if (lastKnownItem != null) {
                            try {
                                viewModel.restoreItem(lastKnownItem.item, lastKnownItem.metadata);
                                Snackbar.make(btnDelete, "Item restored", Snackbar.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Snackbar.make(btnDelete, "Failed to restore item", Snackbar.LENGTH_LONG).show();
                            }
                        }
                    })
                    .show();

            finish();
        });

        // --- Return to inventory ---
        btnBack.setOnClickListener(v -> {
            Snackbar.make(btnBack, "Returning to inventory", Snackbar.LENGTH_LONG).show();
            finish();
        });
    }

    /**
     * Bind item details to UI components.
     * LiveData ensures this is always called with the latest DB state.
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
}