package com.netcracker.hotelbe.service.validation;

import com.netcracker.hotelbe.entity.UnavailableApartment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.validation.ConstraintViolation;
import java.sql.Date;
import java.util.Set;

@Service
public class UnavailableApartmentValidator implements Validator {

    @Autowired
    private javax.validation.Validator validator;

    @Override
    public boolean supports(Class<?> aClass) {
        return UnavailableApartment.class.equals(aClass);
    }

    @Override
    public void validate(final Object o, Errors errors) {
        Set<ConstraintViolation<Object>> validates = validator.validate(o);

        validates.forEach(
                cv -> errors.rejectValue(
                        cv.getPropertyPath().toString(),
                        "",
                        cv.getMessage())
        );

        UnavailableApartment unavailableApartment = (UnavailableApartment) o;

        Date currentTime = new Date(System.currentTimeMillis() - 120000);

        if (unavailableApartment.getStartDate().compareTo(currentTime) < 0) {
            errors.rejectValue("startDate","", "Start date cant be before current date ");
        }
        if (unavailableApartment.getEndDate().compareTo(currentTime) < 0) {
            errors.rejectValue("endDate","", "End date cant be before current date ");
        }
        if (unavailableApartment.getEndDate().compareTo(unavailableApartment.getStartDate()) < 0){
            errors.rejectValue("endDate", "", "End date cant be before start date");
        }
    }
}
