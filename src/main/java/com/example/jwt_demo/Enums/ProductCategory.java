package com.example.jwt_demo.Enums;

public enum ProductCategory {

    Furniture("Furniture"),
    Electricity("Electricity"),
    Electronics("Electronics"),
    Computers("Computers"),
    Phones("Phones"),
    Audio("Audio"),
    Gaming("Gaming"),
    Accessories("Accessories"),
    Home("Home"),
    Kitchen("Kitchen"),
    Sports("Sports"),
    Clothing("Clothing"),
    Shoes("Shoes"),
    Beauty("Beauty"),
    Books("Books"),
    Toys("Toys"),
    Automotive("Automotive"),
    Garden("Garden"),
    Office("Office"),
    Pets("Pets"),
    Health("Health"),
    Food("Food"),
    Tools("Tools"),
    Cameras("Cameras"),
    SmartHome("Smart Home");

    private String displayName;

    ProductCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}