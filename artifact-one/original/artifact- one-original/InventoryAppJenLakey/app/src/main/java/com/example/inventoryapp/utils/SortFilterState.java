package com.example.inventoryapp.utils;

import com.example.inventoryapp.model.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * SortFilterState
 * ---------------
 * Utility class that encapsulates the current sort and filter state
 * for inventory queries. Used by repositories or view models to
 * determine how to fetch and display items.
 * Includes applyFilters() to filter/sort a list of Item DTOs.
 *
 * Notes:
 * - Metadata fields (description, location) are optional.
 * - Filters should include items where metadata is missing (null),
 *   not exclude them outright.
 */
public class SortFilterState {

    // --- Sort options ---
    public enum SortOption {
        NAME_ASC,        // sort by item name (ascending)
        QUANTITY_ASC,    // sort by quantity (ascending)
        QUANTITY_DESC,   // sort by quantity (descending)
        CATEGORY_ASC     // sort by category (ascending)
    }

    // --- Filter fields ---
    public enum FilterField {
        NONE,            // no filter applied
        ITEM_NAME,       // filter by item name
        CATEGORY,        // filter by category
        DESCRIPTION,     // filter by description
        LOCATION         // filter by location
    }

    // --- Current state ---
    private SortOption currentSort;
    private FilterField currentFilterField;
    private String currentFilterKeyword;

    /**
     * Default constructor initializes to:
     * - Sort by name ascending
     * - No filter
     */
    public SortFilterState() {
        this.currentSort = SortOption.NAME_ASC;
        this.currentFilterField = FilterField.NONE;
        this.currentFilterKeyword = "";
    }

    // --- Sort accessors ---
    public SortOption getCurrentSort() { return currentSort; }
    public void setCurrentSort(SortOption sort) { this.currentSort = sort; }

    // --- Filter accessors ---
    public FilterField getCurrentFilterField() { return currentFilterField; }
    public void setCurrentFilterField(FilterField field) { this.currentFilterField = field; }

    public String getCurrentFilterKeyword() { return currentFilterKeyword; }
    public void setCurrentFilterKeyword(String keyword) { this.currentFilterKeyword = keyword; }

    // --- Helpers ---
    /** @return true if a filter is active (field != NONE and keyword not empty) */
    public boolean hasFilter() {
        return currentFilterField != FilterField.NONE &&
                currentFilterKeyword != null &&
                !currentFilterKeyword.isEmpty();
    }

    /** Clears the current filter state. */
    public void clearFilter() {
        this.currentFilterField = FilterField.NONE;
        this.currentFilterKeyword = "";
    }

    // --- Apply filters and sorting to a list of Items ---
    public List<Item> applyFilters(List<Item> items) {
        if (items == null) return new ArrayList<>();

        List<Item> filtered = new ArrayList<>(items);

        // Apply filter if active
        if (hasFilter()) {
            String keywordLower = currentFilterKeyword.toLowerCase();
            switch (currentFilterField) {
                case ITEM_NAME:
                    filtered.removeIf(i -> i.getName() == null ||
                            !i.getName().toLowerCase().contains(keywordLower));
                    break;
                case CATEGORY:
                    filtered.removeIf(i -> i.getCategory() == null ||
                            !i.getCategory().toLowerCase().contains(keywordLower));
                    break;
                case DESCRIPTION:
                    // Include items if description matches OR description is missing (null)
                    filtered.removeIf(i -> i.getDescription() != null &&
                            !i.getDescription().toLowerCase().contains(keywordLower));
                    break;
                case LOCATION:
                    // Include items if location matches OR location is missing (null)
                    filtered.removeIf(i -> i.getLocation() != null &&
                            !i.getLocation().toLowerCase().contains(keywordLower));
                    break;
                default:
                    break;
            }
        }

        // Apply sort
        switch (currentSort) {
            case NAME_ASC:
                Collections.sort(filtered, Comparator.comparing(Item::getName, String.CASE_INSENSITIVE_ORDER));
                break;
            case QUANTITY_ASC:
                Collections.sort(filtered, Comparator.comparingInt(Item::getQuantity));
                break;
            case QUANTITY_DESC:
                Collections.sort(filtered, (a, b) -> Integer.compare(b.getQuantity(), a.getQuantity()));
                break;
            case CATEGORY_ASC:
                Collections.sort(filtered, Comparator.comparing(Item::getCategory, String.CASE_INSENSITIVE_ORDER));
                break;
        }

        return filtered;
    }
}