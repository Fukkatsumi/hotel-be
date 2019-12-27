package com.netcracker.hotelbe.utils.enums;

public enum RegEx {

    LONG("^\\d*$"),
    DATE("(^\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2})|(^\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2})|(^\\d{4}-\\d{2}-\\d{2}\\s\\d{2})|(^\\d{4}-\\d{2}-\\d{2})");

    private String fullName;

    RegEx(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }
}
