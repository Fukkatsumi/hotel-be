package com.netcracker.hotelbe.service.validation;

import com.netcracker.hotelbe.entity.ApartmentClass;
import com.netcracker.hotelbe.service.ApartmentClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.validation.ConstraintViolation;
import java.util.Set;

@Service
public class ApartmentClassValidator implements Validator {
    @Autowired
    private javax.validation.Validator validator;

    @Autowired
    private ApartmentClassService apartmentClassService;

    @Override
    public boolean supports(Class<?> aClass) {
        return ApartmentClass.class.equals(aClass);
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

        ApartmentClass apartmentClass = (ApartmentClass) o;

        if (apartmentClass.getNumberOfRooms() <= 0) {
            errors.rejectValue("numberOfRooms", "", "Number of rooms cant be less then 1 ");
        }
        if (apartmentClass.getNumberOfCouchette() <= 0) {
            errors.rejectValue("numberOfCouchette", "", "Number of couchette cant be less then 1");
        }
    }
}
