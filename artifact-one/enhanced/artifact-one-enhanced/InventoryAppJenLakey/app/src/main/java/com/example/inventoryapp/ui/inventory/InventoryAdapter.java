package com.example.inventoryapp.ui.inventory;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventoryapp.R;
import com.example.inventoryapp.model.Item;
import com.example.inventoryapp.ui.item.ItemDetailActivity;

import java.util.List;

/**
 * InventoryAdapter
 * ----------------
 * RecyclerView adapter for displaying inventory items in a grid/list.
 *
 * Responsibilities:
 * - Binds immutable Item DTOs to the row views.
 * - Handles row interactions (view details, adjust quantity, delete).
 * - Delegates all persistence operations to InventoryViewModel.
 *
 * Architectural Note:
 * The adapter must never perform database writes directly. All quantity
 * updates and deletions are routed through the ViewModel to maintain
 * lifecycle safety, consistency, and MVVM separation of concerns.
 */
public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    private final Context context;
    private final List<Item> itemList;

    // ViewModel reference for unified quantity update workflow
    private final InventoryViewModel viewModel;

    public InventoryAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;

        // Obtain ViewModel from the Activity context
        this.viewModel = new ViewModelProvider((InventoryActivity) context)
                .get(InventoryViewModel.class);
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
        holder.tvGridCategory.setText(item.getCategory() != null ? item.getCategory() : "");
        holder.tvGridQuantity.setText(context.getString(R.string.quantity_label, item.getQuantity()));

        // --- Row click: navigate to ItemDetailActivity ---
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ItemDetailActivity.class);
            intent.putExtra("item_id", item.getId());
            context.startActivity(intent);
        });

        // --- Decrease quantity (unified workflow) ---
        holder.btnDecreaseRow.setOnClickListener(v -> {
            int current = item.getQuantity();
            if (current > 0) {
                int delta = -1;

                // Update UI immediately for responsiveness
                Item updated = new Item(
                        item.getId(),
                        item.getName(),
                        item.getCategory(),
                        item.getDescription(),
                        item.getLocation(),
                        current - 1
                );
                itemList.set(position, updated);
                notifyItemChanged(position);

                // Delegate persistence to ViewModel
                viewModel.updateQuantity(item.getId(), delta);
            }
        });

        // --- Increase quantity (unified workflow) ---
        holder.btnIncreaseRow.setOnClickListener(v -> {
            int delta = +1;

            // Update UI immediately for responsiveness
            Item updated = new Item(
                    item.getId(),
                    item.getName(),
                    item.getCategory(),
                    item.getDescription(),
                    item.getLocation(),
                    item.getQuantity() + 1
            );
            itemList.set(position, updated);
            notifyItemChanged(position);

            // Delegate persistence to ViewModel
            viewModel.updateQuantity(item.getId(), delta);
        });

        // --- Delete row (unified workflow) ---
        holder.btnDeleteRow.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                Item toRemove = itemList.get(adapterPosition);

                // Update UI immediately
                itemList.remove(adapterPosition);
                notifyItemRemoved(adapterPosition);

                // Delegate deletion to ViewModel
                viewModel.deleteItemById(toRemove.getId());
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
     * removeAt()
     * ----------
     * This method is not invoked in the current UI flow, but it is kept
     * intentionally for future extensibility. Features such as swipe‑to‑delete,
     * batch deletion, or ViewModel‑driven row removal will require a direct
     * adapter‑level removal method. Retaining this method prevents additional
     * refactoring when those enhancements are implemented.
     * The @SuppressWarnings annotation acknowledges that the method is
     * intentionally unused at this stage.
     */
    @SuppressWarnings("unused")
    public void removeAt(int position) {
        if (position >= 0 && position < itemList.size()) {
            Item toRemove = itemList.get(position);

            // Update UI immediately
            itemList.remove(position);
            notifyItemRemoved(position);

            // Delegate deletion to ViewModel
            viewModel.deleteItemById(toRemove.getId());
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