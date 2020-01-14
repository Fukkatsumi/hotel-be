package com.netcracker.hotelbe.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.sql.Date;

@Data
@Entity
@Table(name = "UnavailableApartments",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class UnavailableApartment {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @Column(name = "end_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    @Column(name = "cause_description")
    private String causeDescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_apartment")
    private Apartment apartment;

}
