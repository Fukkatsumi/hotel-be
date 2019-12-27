package com.netcracker.hotelbe.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "ApartmentPrices",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ApartmentPrice implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "price")
    @Min(value = 0, message = "Price cant be less then 0")
    private int price;

    @Column(name = "start_period")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp startPeriod;

    @Column(name = "end_period")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp endPeriod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apartmentclass_id")
    private ApartmentClass apartmentClass;

}
