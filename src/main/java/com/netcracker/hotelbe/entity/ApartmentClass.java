package com.netcracker.hotelbe.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

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
    private int numberOfRooms;

    @Column(name = "number_of_couchette")
    private int numberOfCouchette;

    public ApartmentClass() {
    }

    public ApartmentClass(String nameClass, int numberOfRooms, int numberOfCouchette) {
        this.nameClass = nameClass;
        this.numberOfRooms = numberOfRooms;
        this.numberOfCouchette = numberOfCouchette;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNameClass() {
        return nameClass;
    }

    public void setNameClass(String nameClass) {
        this.nameClass = nameClass;
    }

    public int getNumberOfRooms() {
        return numberOfRooms;
    }

    public void setNumberOfRooms(int numberOfRooms) {
        this.numberOfRooms = numberOfRooms;
    }

    public int getNumberOfCouchette() {
        return numberOfCouchette;
    }

    public void setNumberOfCouchette(int numberOfCouchette) {
        this.numberOfCouchette = numberOfCouchette;
    }
}
