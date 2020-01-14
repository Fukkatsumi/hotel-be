package com.netcracker.hotelbe.service.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.validation.ConstraintViolation;
import java.util.Map;
import java.util.Set;

@Service
public class BookingAddServiceValidator implements Validator {
    @Autowired
    private javax.validation.Validator validator;


    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }

    @Override
    public void validate(Object o, Errors errors) {
        Set<ConstraintViolation<Object>> validates = validator.validate(o);

        validates.forEach(
                cv -> errors.rejectValue(
                        cv.getPropertyPath().toString(),
                        "",
                        cv.getMessage())
        );

        Map<String, Long> values = (Map<String, Long>) o;

        if (!values.containsKey("id")) {
            errors.rejectValue("id", "", "id cannot be empty");
        }
        if (!values.containsKey("countServices")) {
            errors.rejectValue("id", "", "countServices cannot be empty");
        }
    }
}
