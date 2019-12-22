package com.netcracker.hotelbe.service;

import com.netcracker.hotelbe.entity.Apartment;
import com.netcracker.hotelbe.entity.UnavailableApartment;
import com.netcracker.hotelbe.repository.UnavailableApartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class UnavailableApartmentService {

    @Autowired
    private UnavailableApartmentRepository unavailableApartmentRepository;

    @Autowired
    private ApartmentService apartmentService;

    @Autowired
    @Qualifier("unavailableApartmentValidator")
    private Validator unavailableApartmentValidator;

    public List<UnavailableApartment> getAll() {
        return unavailableApartmentRepository.findAll();
    }

    public UnavailableApartment save(UnavailableApartment unavailableApartment) {
        final Apartment apartment = apartmentService.findById(unavailableApartment.getApartment().getId());
        unavailableApartment.setApartment(apartment);

        return unavailableApartmentRepository.save(unavailableApartment);
    }

    public UnavailableApartment findById(final Long id) {
        return unavailableApartmentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );
    }

    public UnavailableApartment update(final UnavailableApartment unavailableApartment, final Long id) {
        unavailableApartmentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );

        final Apartment apartment = apartmentService.findById(unavailableApartment.getApartment().getId());

        unavailableApartment.setApartment(apartment);
        unavailableApartment.setId(id);

        return unavailableApartmentRepository.save(unavailableApartment);
    }

    public void deleteById(final Long id) {
        final UnavailableApartment delete = unavailableApartmentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );

        unavailableApartmentRepository.delete(delete);
    }

    public void validate(final UnavailableApartment unavailableApartment, BindingResult bindingResult) throws MethodArgumentNotValidException {
        unavailableApartmentValidator.validate(unavailableApartment, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new MethodArgumentNotValidException(null, bindingResult);
        }
    }
}
