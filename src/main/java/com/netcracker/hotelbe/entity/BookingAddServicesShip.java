package com.netcracker.hotelbe.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Entity
@Table(name = "BookingAddServicesShip",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class BookingAddServicesShip implements Serializable {

    @Id
    @NotNull
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "add_service_id_booking")
    private BookingAddServices bookingAddServices;

    @NotNull
    @Column(name = "count_services")
    @Min(value = 1, message = "Count services cant be less than 1")
    private int countServices;

}
