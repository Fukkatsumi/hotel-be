package com.netcracker.hotelbe.service;

import com.netcracker.hotelbe.entity.Apartment;
import com.netcracker.hotelbe.entity.UnavailableApartment;
import com.netcracker.hotelbe.repository.UnavailableApartmentRepository;
import com.netcracker.hotelbe.service.filter.FilterService;
import com.netcracker.hotelbe.utils.enums.MathOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.persistence.EntityNotFoundException;
import java.sql.Date;
import java.util.List;
import java.util.Map;

@Service
public class UnavailableApartmentService {

    @Autowired
    private UnavailableApartmentRepository unavailableApartmentRepository;

    @Autowired
    private ApartmentService apartmentService;

    @Autowired
    @Qualifier("unavailableApartmentValidator")
    private Validator unavailableApartmentValidator;

    @Autowired
    private FilterService filterService;

    @Autowired
    private EntityService entityService;

    public List<UnavailableApartment> getAll() {
        List<UnavailableApartment> unavailableApartments =  unavailableApartmentRepository.findAll();
        unavailableApartments.forEach(this::correctingDate);

        return unavailableApartments;
    }

    public List<UnavailableApartment> getAllByParams(Map<String, String> allParams) {
        List<UnavailableApartment> unavailableApartments;
        if(allParams.size()!=0) {
            unavailableApartments = unavailableApartmentRepository.findAll(filterService.fillFilter(allParams, UnavailableApartment.class));
        } else {
            unavailableApartments = unavailableApartmentRepository.findAllNative();
        }
        unavailableApartments.forEach(this::correctingDate);

        return unavailableApartments;
    }

    public UnavailableApartment save(UnavailableApartment unavailableApartment) {
        final Apartment apartment = apartmentService.findById(unavailableApartment.getApartment().getId());
        unavailableApartment.setApartment(apartment);

        return unavailableApartmentRepository.save(unavailableApartment);
    }

    public UnavailableApartment findById(final Long id) {
        UnavailableApartment unavailableApartment = unavailableApartmentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );

        return correctingDate(unavailableApartment);
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

    public UnavailableApartment patch(Long id, Map<String, Object> updates) {
        UnavailableApartment unavailableApartment = unavailableApartmentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );

        return unavailableApartmentRepository.save((UnavailableApartment) entityService.fillFields(updates, unavailableApartment));
    }

    public void validate(final UnavailableApartment unavailableApartment, BindingResult bindingResult) throws MethodArgumentNotValidException {
        unavailableApartmentValidator.validate(unavailableApartment, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new MethodArgumentNotValidException(null, bindingResult);
        }
    }

    private UnavailableApartment correctingDate(UnavailableApartment unavailableApartment){
        Date startDate= entityService.correctingDate(unavailableApartment.getStartDate(), MathOperation.PLUS, 1);
        unavailableApartment.setStartDate(startDate);

        Date endDate = entityService.correctingDate(unavailableApartment.getEndDate(), MathOperation.PLUS, 1);
        unavailableApartment.setEndDate(endDate);

        return unavailableApartment;
    }

    public UnavailableApartment correctingDateMinus(UnavailableApartment unavailableApartment){
        Date startDate= entityService.correctingDate(unavailableApartment.getStartDate(), MathOperation.MINUS, 1);
        unavailableApartment.setStartDate(startDate);

        Date endDate = entityService.correctingDate(unavailableApartment.getEndDate(), MathOperation.MINUS, 1);
        unavailableApartment.setEndDate(endDate);

        return unavailableApartment;
    }
}
