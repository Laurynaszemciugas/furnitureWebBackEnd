package com.example.jwt_demo.Enums;

public enum Type {

    IN("In"),
    OUT("Out"),
    SAME("Same");

    private String displayName;

    Type(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
