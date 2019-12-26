package com.netcracker.hotelbe.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.netcracker.hotelbe.entity.enums.BookingStatus;
import com.netcracker.hotelbe.utils.PostgreSQLEnumType;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@Data
@Entity
@Table(name = "Bookings",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
@TypeDef(
        name = "pgsql_enum",
        typeClass = PostgreSQLEnumType.class
)
public class Booking implements Serializable {
    @Id
    @NotNull
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "start_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @Column(name = "end_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    @Column(name = "total_price")
    @Min(value = 0, message = "Total price cant be less than 0")
    private int totalPrice;

    @Column(name = "comments")
    private String comment;

    @Column(name = "created_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createdDate;

    @Column(name = "review")
    private String review;

    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status")
    @Type( type = "pgsql_enum" )
    private BookingStatus bookingStatus;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name="apartmentclass_id")
    private ApartmentClass apartmentClass;

    @ManyToOne
    @JoinColumn(name="apartment_id")
    private Apartment apartment;

    @JsonIgnore
    @OneToMany(mappedBy = "booking", fetch = FetchType.LAZY)
    private List<BookingAddServicesShip> bookingAddServicesShips;

}
