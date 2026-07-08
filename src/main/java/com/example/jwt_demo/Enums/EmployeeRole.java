package com.example.jwt_demo.Enums;

public enum EmployeeRole {

    INTERN("Intern"),
    JUNIOR_WORKER("Junior Worker"),
    ASSEMBLER("Assembler"),
    CARPENTER("Carpenter"),
    PAINTER("Painter"),
    FINISHER("Finisher"),
    QUALITY_CHECKER("Quality Checker"),
    WAREHOUSE_WORKER("Warehouse Worker"),
    LOGISTICS_WORKER("Logistics Worker"),
    DELIVERY_DRIVER("Delivery Driver"),
    SUPERVISOR("Supervisor"),
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