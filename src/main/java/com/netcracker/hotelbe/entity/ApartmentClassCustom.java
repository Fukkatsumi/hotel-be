package com.netcracker.hotelbe.entity;

import java.util.ArrayList;
import java.util.List;

public class ApartmentClassCustom {
    /**
     * @countOfApartments - quantity free apartments according to booked apartmentClass in "Booking" and booked apartments
     * @apartmentClass - unique object
     * @aparmentPriceOnDates - price on dates according to "ApartmentPrice"
     */
    private Integer countOfApartments;
    private ApartmentClass apartmentClass;
    private Integer apartmentPriceOnDates;

    public ApartmentClassCustom(ApartmentClass apartmentClass) {
        this.apartmentClass = apartmentClass;
        this.countOfApartments = 0;
    }

    public void setApartmentClass(ApartmentClass apartmentClass) {
        this.apartmentClass = apartmentClass;
    }

    public ApartmentClass getApartmentClass() {
        return apartmentClass;
    }

    public Integer getCountOfApartments() {
        return countOfApartments;
    }

    public void setCountOfApartments(Integer countOfApartments) {
        this.countOfApartments = countOfApartments;
    }

    public Integer getApartmentPriceOnDates() {
        return apartmentPriceOnDates;
    }

    public void setApartmentPriceOnDates(Integer apartmentPriceOnDates) {
        this.apartmentPriceOnDates = apartmentPriceOnDates;
    }
}
