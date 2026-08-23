package com.example.jwt_demo.Enums;

public enum TimeZone {

    UTC("UTC"),
    VILNIUS("Europe/Vilnius"),
    LONDON("Europe/London"),
    NEW_YORK("America/New_York"),
    LOS_ANGELES("America/Los_Angeles"),
    TOKYO("Asia/Tokyo"),
    BERLIN("Europe/Berlin");

    private final String zoneId;

    TimeZone(String zoneId) {
        this.zoneId = zoneId;
    }

    public String getZoneId() {
        return zoneId;
    }


}
