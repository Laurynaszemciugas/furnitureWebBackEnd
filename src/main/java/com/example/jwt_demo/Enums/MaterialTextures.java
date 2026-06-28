package com.example.jwt_demo.Enums;

public enum MaterialTextures {


    SMOOTH("Smooth"),
    ROUGH("Rough"),
    MATTE("Matte"),
    GLOSSY("Glossy"),
    WOOD_GRAIN("Wood Grain"),
    BRUSHED_METAL("Brushed Metal"),
    POLISHED("Polished"),
    FABRIC_WOVEN("Fabric Woven"),
    LEATHER_GRAIN("Leather Grain"),
    RUSTIC("Rustic"),
    NATURAL("Natural"),
    PAINTED("Painted"),
    LAMINATED("Laminated"),
    VARNISHED("Varnished"),
    OTHER("Other");

    private final String displayName;

    MaterialTextures(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }


}
