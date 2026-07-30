package com.example.jwt_demo.Enums;

public enum Widths {

    FULL_WIDTH("100%"),
    HALF_WIDTH("calc(50% - 8px)"),
    THREE_QUARTS("calc(75% - 8px)"),
    QUARTER_WIDTH("calc(25% - 8px)");

    private final String width;

    Widths(String width) {
        this.width = width;
    }

    public String getWidth() {
        return width;
    }
}
