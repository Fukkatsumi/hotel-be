package com.netcracker.hotelbe.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.netcracker.hotelbe.entity.enums.UserRole;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "login", unique = true)
    @Size(min = 5, message = "Login length cant be less then 5 symbols")
    @Size(max = 20, message = "Login length cant be more then 20 symbols")
    @NotNull
    private String login;

    @Column(name = "password")
    @NotNull
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(name = "user_role")
    @Enumerated(EnumType.STRING)
    @Type(type = "pgsql_enum")
    @NotNull
    private UserRole userRole;

    @Column(name = "first_name")
    @Pattern(regexp = "^\\w+", message = "Name cant contain characters except letters")
    @NotNull
    private String firstname;

    @Column(name = "last_name")
    @Pattern(regexp = "^\\w+", message = "Lastname cant contain characters except letters")
    @NotNull
    private String lastname;

    @Column(name = "email", unique = true)
    @Pattern(regexp = "^\\S+@.+\\..+", message = "Invalid email")
    @NotNull
    private String email;

    @Column(name = "phone_number", unique = true)
    @NotNull
    private String phoneNumber;

    @Column(name = "points")
    @Min(value = 0, message = "Points number cant be less then 0")
    private int points;

    @JsonBackReference
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
    @ToString.Exclude
    private Staff staff;

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Booking> bookings;

}
