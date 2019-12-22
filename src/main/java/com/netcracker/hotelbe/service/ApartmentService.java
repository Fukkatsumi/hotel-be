package com.netcracker.hotelbe.service;

import com.netcracker.hotelbe.entity.Apartment;
import com.netcracker.hotelbe.entity.ApartmentClass;
import com.netcracker.hotelbe.repository.ApartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class ApartmentService {

    @Autowired
    private ApartmentRepository apartmentRepository;

    @Autowired
    private ApartmentClassService apartmentClassService;

    public List<Apartment> getAll() {
        return apartmentRepository.findAll();
    }

    public Apartment save(Apartment apartment) {
        final ApartmentClass apartmentClass = apartmentClassService.findById(apartment.getApartmentClass().getId());
        apartment.setApartmentClass(apartmentClass);

        return apartmentRepository.save(apartment);
    }

    public Apartment findById(final Long id) {
        return apartmentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );
    }

    public Apartment update(Apartment apartment, final Long id) {
        apartmentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );

        final ApartmentClass apartmentClass = apartmentClassService.findById(apartment.getApartmentClass().getId());

        apartment.setApartmentClass(apartmentClass);
        apartment.setId(id);

        return apartmentRepository.save(apartment);
    }

    public void deleteById(final Long id) {

        final Apartment delete = apartmentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );

        apartmentRepository.delete(delete);
    }
}
