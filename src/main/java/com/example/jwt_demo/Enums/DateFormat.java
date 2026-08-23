package com.example.jwt_demo.Enums;

public enum DateFormat {


    DD_MM_YYYY("dd/MM/yyyy"),
    DD_MM_YYYY_DASH("dd-MM-yyyy"),
    YYYY_MM_DD("yyyy-MM-dd"),
    YYYY_MM_DD_DASH("yyyy-MM-dd"),
    MM_DD_YYYY("MM/dd/yyyy"),

    DD_MM_YYYY_HH_MM("dd/MM/yyyy HH:mm"),
    DD_MM_YYYY_HH_MM_SS("dd/MM/yyyy HH:mm:ss"),

    YYYY_MM_DD_HH_MM("yyyy-MM-dd HH:mm"),
    YYYY_MM_DD_HH_MM_SS("yyyy-MM-dd HH:mm:ss");

    private final String pattern;

    DateFormat(String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }


}
