package com.netcracker.hotelbe.entity;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.netcracker.hotelbe.entity.enums.StaffSpeciality;
import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "staff")
@Data
@JsonAutoDetect
public class Staff {

    @Id
    @Column(name = "id")
    private Long id;

    @MapsId
    @OneToOne
    @JoinColumn(name = "id")
    @JsonManagedReference
    private User user;

    @Column(name = "spec")
    @NotNull
    @Enumerated(EnumType.STRING)
    @Type(type = "pgsql_enum")
    private StaffSpeciality speciality;

    @Column(name = "isactive")
    private boolean active;

}
