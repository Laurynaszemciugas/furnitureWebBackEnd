package com.example.jwt_demo.Enums;

public enum SortOrder {

    NEWEST("Newest"),
    OLDEST("Oldest"),
    ALL("All");

    private final String displayName;

    SortOrder(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}