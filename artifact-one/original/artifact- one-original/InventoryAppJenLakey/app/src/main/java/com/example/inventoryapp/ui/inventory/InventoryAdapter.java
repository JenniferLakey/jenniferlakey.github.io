package com.example.inventoryapp.ui.inventory;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventoryapp.R;
import com.example.inventoryapp.db.DbExecutor;
import com.example.inventoryapp.model.Inventory;
import com.example.inventoryapp.model.Item;
import com.example.inventoryapp.repository.InventoryRepository;
import com.example.inventoryapp.ui.item.ItemDetailActivity;

import java.util.List;

/**
 * InventoryAdapter
 * ----------------
 * RecyclerView adapter for displaying inventory items in a grid/list.
 * - Binds immutable Item DTOs to the row views.
 * - Handles row interactions (view details, adjust quantity, delete).
 * - Delegates persistence operations to InventoryRepository on a background executor.
 * Best practice: adapter focuses on UI binding; repository handles DB logic.
 */
public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    private static final String TAG = "InventoryAdapter";

    private final Context context;
    private final List<Item> itemList;
    private final InventoryRepository repo;

    public InventoryAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
        this.repo = new InventoryRepository(context);
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_inventory, parent, false);
        return new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        Item item = itemList.get(position);

        // --- Bind text fields ---
        holder.tvGridItemName.setText(item.getName());
        // Null-safe fallback: show empty string if category is null
        holder.tvGridCategory.setText(item.getCategory() != null ? item.getCategory() : "");
        holder.tvGridQuantity.setText(context.getString(R.string.quantity_label, item.getQuantity()));

        // --- Row click: navigate to ItemDetailActivity ---
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ItemDetailActivity.class);
            intent.putExtra("item_id", item.getId());
            context.startActivity(intent);
        });

        // --- Decrease quantity and persist ---
        holder.btnDecreaseRow.setOnClickListener(v -> {
            int current = item.getQuantity();
            if (current > 0) {
                int newQty = current - 1;

                // Create a new immutable Item with updated quantity
                Item updated = new Item(
                        item.getId(),
                        item.getName(),
                        item.getCategory(),
                        item.getDescription(),
                        item.getLocation(),
                        newQty
                );

                // Replace in adapter list and refresh row
                itemList.set(position, updated);
                notifyItemChanged(position);

                // Persist change in background
                DbExecutor.IO.execute(() -> {
                    try {
                        repo.updateQuantity(item.getId(), newQty);
                        Log.i(TAG, "Quantity decreased for item ID " + item.getId() + " to " + newQty);
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to decrease quantity: " + e.getMessage(), e);
                    }
                });
            }
        });

        // --- Increase quantity and persist ---
        holder.btnIncreaseRow.setOnClickListener(v -> {
            int newQty = item.getQuantity() + 1;

            // Create a new immutable Item with updated quantity
            Item updated = new Item(
                    item.getId(),
                    item.getName(),
                    item.getCategory(),
                    item.getDescription(),
                    item.getLocation(),
                    newQty
            );

            // Replace in adapter list and refresh row
            itemList.set(position, updated);
            notifyItemChanged(position);

            // Persist change in background
            DbExecutor.IO.execute(() -> {
                try {
                    repo.updateQuantity(item.getId(), newQty);
                    Log.i(TAG, "Quantity increased for item ID " + item.getId() + " to " + newQty);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to increase quantity: " + e.getMessage(), e);
                }
            });
        });

        // --- Delete row and persist ---
        holder.btnDeleteRow.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                Item toRemove = itemList.get(adapterPosition);
                int itemId = toRemove.getId();

                // Update UI immediately
                itemList.remove(adapterPosition);
                notifyItemRemoved(adapterPosition);

                // Persist deletion in background
                DbExecutor.IO.execute(() -> {
                    try {
                        // Construct Inventory entity with correct PK for @Delete
                        Inventory toDelete = new Inventory(itemId, toRemove.getName(),
                                toRemove.getQuantity(), toRemove.getCategory());
                        int deleted = repo.deleteItemCascade(toDelete);
                        Log.i(TAG, "Deleted item ID " + itemId + " (rows affected: " + deleted + ")");
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to delete item ID " + itemId + ": " + e.getMessage(), e);
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }

    /**
     * Replace adapter dataset and refresh UI.
     */
    public void updateData(List<Item> newItems) {
        itemList.clear();
        itemList.addAll(newItems);
        notifyDataSetChanged();
    }

    /**
     * Remove item at given position safely and persist deletion.
     */
    public void removeAt(int position) {
        if (position >= 0 && position < itemList.size()) {
            Item toRemove = itemList.get(position);
            int itemId = toRemove.getId();

            // Update UI immediately
            itemList.remove(position);
            notifyItemRemoved(position);

            // Persist deletion in background
            DbExecutor.IO.execute(() -> {
                try {
                    Inventory toDelete = new Inventory(itemId, toRemove.getName(),
                            toRemove.getQuantity(), toRemove.getCategory());
                    int deleted = repo.deleteItemCascade(toDelete);
                    Log.i(TAG, "Deleted item ID " + itemId + " via removeAt (rows affected: " + deleted + ")");
                } catch (Exception e) {
                    Log.e(TAG, "Failed to delete item via removeAt for ID " + itemId + ": " + e.getMessage(), e);
                }
            });
        }
    }

    /**
     * ViewHolder class for inventory row.
     */
    static class InventoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvGridItemName, tvGridCategory, tvGridQuantity;
        ImageButton btnIncreaseRow, btnDecreaseRow, btnDeleteRow;

        InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGridItemName = itemView.findViewById(R.id.tvGridItemName);
            tvGridCategory = itemView.findViewById(R.id.tvGridCategory);
            tvGridQuantity = itemView.findViewById(R.id.tvGridQuantity);
            btnIncreaseRow = itemView.findViewById(R.id.btnIncreaseRow);
            btnDecreaseRow = itemView.findViewById(R.id.btnDecreaseRow);
            btnDeleteRow = itemView.findViewById(R.id.btnDeleteRow);
        }
    }
}