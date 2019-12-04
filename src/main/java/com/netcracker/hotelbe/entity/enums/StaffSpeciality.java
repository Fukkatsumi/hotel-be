package com.netcracker.hotelbe.entity.enums;


import java.io.Serializable;

public enum StaffSpeciality implements Serializable {

    Cleaner("Cleaner"),
    Handyman("Handyman"),
    Manager("Manager"),
    Hotel_Administrator("Hotel_Administrator");

    private String fullName;

    StaffSpeciality(String fullName) {
        this.fullName = fullName;
    }

    String getFullName() {
        return fullName;
    }
}
