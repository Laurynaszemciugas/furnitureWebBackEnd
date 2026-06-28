package com.example.jwt_demo.Enums;

public enum MaterialUnits {


        // Weight
        KG("Kilogram", "kg"),
        GRAM("Gram", "g"),
        TON("Ton", "t"),

        // Length
        METER("Meter", "m"),
        CENTIMETER("Centimeter", "cm"),
        MILLIMETER("Millimeter", "mm"),

        // Area
        SQUARE_METER("Square Meter", "m²"),
        SQUARE_CENTIMETER("Square Centimeter", "cm²"),

        // Volume
        LITER("Liter", "L"),
        MILLILITER("Milliliter", "ml"),
        CUBIC_METER("Cubic Meter", "m³"),

        // Count
        PCS("Pieces", "pcs"),
        PACK("Pack", "pack"),
        BOX("Box", "box"),
        ROLL("Roll", "roll"),
        SHEET("Sheet", "sheet"),

        // Furniture-specific
        PANEL("Panel", "panel"),
        BOARD("Board", "board"),
        PLANK("Plank", "plank"),
        BUNDLE("Bundle", "bundle"),

        OTHER("Other", "other");

        private final String displayName;
        private final String symbol;

    MaterialUnits(String displayName, String symbol) {
            this.displayName = displayName;
            this.symbol = symbol;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getSymbol() {
            return symbol;
        }
    }

