package com.netcracker.hotelbe.entity;

import java.util.ArrayList;
import java.util.List;

public class ApartmentClassCustom {
    /**
     * @countOfApartments - quantity free apartments according to booked apartmentClass in "Booking" and booked apartments
     * @apartmentClass - unique object
     * @apatmemtList - list of apartments which are NOT booked in "Booking"
     * @aparmentPriceOnDates - price on dates according to "ApartmentPrice"
     */
    private Integer countOfApartments;
    private ApartmentClass apartmentClass;
    private List<Apartment> apartmentList;
    private Integer apartmentPriceOnDates;

    public ApartmentClassCustom(ApartmentClass apartmentClass) {
        this.apartmentClass = apartmentClass;
        this.countOfApartments = 0;
        this.apartmentList = new ArrayList<>();
    }

    public void setApartmentClass(ApartmentClass apartmentClass) {
        this.apartmentClass = apartmentClass;
    }

    public List<Apartment> getApartmentList() {
        return apartmentList;
    }

    public void setApartmentList(List<Apartment> apartmentList) {
        this.apartmentList = apartmentList;
    }

    public void addToApartmentList(Apartment apartment) {
        this.apartmentList.add(apartment);
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
