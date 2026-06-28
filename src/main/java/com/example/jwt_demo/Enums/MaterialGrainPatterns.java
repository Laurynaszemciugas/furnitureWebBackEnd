package com.example.jwt_demo.Enums;

public enum MaterialGrainPatterns {

    STRAIGHT_GRAIN("Straight Grain"),
    WAVY_GRAIN("Wavy Grain"),
    CURLY_GRAIN("Curly Grain"),
    INTERLOCKED_GRAIN("Interlocked Grain"),
    SPIRAL_GRAIN("Spiral Grain"),

    BIRDSEYE("Birdseye"),
    TIGER_STRIPE("Tiger Stripe"),
    QUILTED("Quilted"),
    FIDDLEBACK("Fiddleback"),
    BURL("Burl"),

    FLAME("Flame"),
    RIBBON("Ribbon"),
    CATHEDRAL("Cathedral"),
    RIFT_SAWN("Rift Sawn"),
    QUARTER_SAWN("Quarter Sawn"),
    PLAIN_SAWN("Plain Sawn"),

    FINE_GRAIN("Fine Grain"),
    MEDIUM_GRAIN("Medium Grain"),
    COARSE_GRAIN("Coarse Grain"),

    NO_VISIBLE_GRAIN("No Visible Grain"),
    OTHER("Other");

    private final String displayName;

    MaterialGrainPatterns(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
