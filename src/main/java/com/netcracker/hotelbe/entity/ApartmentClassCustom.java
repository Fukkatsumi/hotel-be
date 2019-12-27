package com.netcracker.hotelbe.entity;

public class ApartmentClassCustom {
    private Integer countOfApartments;
    private ApartmentClass apartmentClass;
    public ApartmentClassCustom(ApartmentClass apartmentClass) {
        this.apartmentClass = apartmentClass;
        this.countOfApartments = 0;
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
}
