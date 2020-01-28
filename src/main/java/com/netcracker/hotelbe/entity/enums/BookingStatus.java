package com.netcracker.hotelbe.entity.enums;

import java.io.Serializable;

public enum BookingStatus implements Serializable {
    Created("Created"),
    Confirmed("Confirmed"),
    CheckedIn("CheckedIn"),
    Confirmed("Confirmed"),
    Closed("Closed"),
    Canceled("Canceled");

    private String fullName;

    BookingStatus(String fullName) {
        this.fullName = fullName;
    }

    String getFullName() {
        return fullName;
    }
}
