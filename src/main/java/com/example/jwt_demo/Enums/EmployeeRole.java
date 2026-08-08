package com.example.jwt_demo.Enums;

public enum EmployeeRole {

    WORKER("Worker"),
    ASSEMBLER("Assembler"),
    CARPENTER("Carpenter"),
    FINISHER("Finisher"),
    WAREHOUSE("Warehouse"),
    MANAGER("Manager"),

    ALL("All");

    private final String displayName;

    EmployeeRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}