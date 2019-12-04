package com.netcracker.hotelbe.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
@Table(name = "ApartmentClass",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ApartmentClass implements Serializable {

    @Id
    @NotNull
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name_class")
    private String nameClass;

    @Column(name = "number_of_rooms")
    @Min(value = 1, message = "Number of rooms cant be less then 1")
    private int numberOfRooms;

    @Column(name = "number_of_couchette")
    @Min(value = 1, message = "Number of couchette cant be less then 1")
    private int numberOfCouchette;

    @JsonIgnore
    @OneToMany(mappedBy = "apartmentClass", fetch = FetchType.LAZY)
    private List<Apartment> apartments;

    @JsonIgnore
    @OneToMany(mappedBy = "apartmentClass", fetch = FetchType.LAZY)
    private List<ApartmentPrice> apartmentPrices;

    @JsonIgnore
    @OneToMany(mappedBy = "apartmentClass", fetch = FetchType.LAZY)
    private List<Booking> bookings;
}
