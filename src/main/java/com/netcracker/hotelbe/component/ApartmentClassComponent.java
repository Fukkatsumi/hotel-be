package com.netcracker.hotelbe.component;

import com.netcracker.hotelbe.entity.ApartmentClass;
import com.netcracker.hotelbe.service.ApartmentClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApartmentClassComponent {

    @Autowired
    private ApartmentClassService apartmentClassService;

    public List<ApartmentClass> getAll(){
       return apartmentClassService.findAll();
    }
}
