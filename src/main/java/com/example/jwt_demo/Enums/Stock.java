package com.example.jwt_demo.Enums;

public enum Stock {

    In_Stock("In Stock"),
    Low_Stock("Low Stock"),
    No_Stock("No Stock"),
    ALL("All");

    private String displayName;

    Stock(String displayName) {
        this.displayName = displayName;
    }


    public String getDisplayName() {
        return displayName;
    }
}
