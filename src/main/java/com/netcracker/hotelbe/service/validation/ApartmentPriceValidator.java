package com.netcracker.hotelbe.service.validation;

import com.netcracker.hotelbe.entity.ApartmentPrice;
import com.netcracker.hotelbe.service.ApartmentPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.validation.ConstraintViolation;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ApartmentPriceValidator implements Validator {

    @Autowired
    private javax.validation.Validator validator;

    @Autowired
    private ApartmentPriceService apartmentPriceService;

    @Override
    public boolean supports(Class<?> aClass) {
        return ApartmentPrice.class.equals(aClass);
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

        ApartmentPrice apartmentPrice = (ApartmentPrice) o;

        Timestamp currentTime = new Timestamp(System.currentTimeMillis() - 120000);

        Date startPeriod = apartmentPrice.getStartPeriod();
        Date endPeriod = apartmentPrice.getEndPeriod();

        if (startPeriod.compareTo(currentTime) < 0) {
            errors.rejectValue("startPeriod", "", "Start period cant be before current date ");
        }
        if (endPeriod.compareTo(currentTime) < 0) {
            errors.rejectValue("endPeriod", "", "End period cant be before current date ");
        }
        if (endPeriod.compareTo(startPeriod) < 0) {
            errors.rejectValue("endPeriod", "", "End period cant be before Start period");
        }
        if (apartmentPrice.getPrice() < 0) {
            errors.rejectValue("price", "", "Price cant be less then 0");
        }

        Map<String, String> values = new HashMap<>();
        values.put("apartmentClass", String.valueOf(apartmentPrice.getApartmentClass().getId()));

        List<ApartmentPrice> apartmentPrices = apartmentPriceService.getAllByParams(values);

        for (ApartmentPrice ap : apartmentPrices) {
            if (startPeriod.compareTo(ap.getStartPeriod()) >= 0
                    && startPeriod.compareTo(ap.getEndPeriod()) <= 0) {
                errors.rejectValue("startPeriod", "", "startPeriod between existing (id=" + ap.getId() + ") startPeriod and endPeriod");
                break;
            }
            if (endPeriod.compareTo(ap.getStartPeriod()) >= 0
                    && endPeriod.compareTo(ap.getEndPeriod()) <= 0) {
                errors.rejectValue("endPeriod", "", "endPeriod between existing (id=" + ap.getId() + ") startPeriod and endPeriod");
                break;
            }
            if (startPeriod.compareTo(ap.getStartPeriod()) <= 0
                    && endPeriod.compareTo(ap.getEndPeriod()) >= 0) {
                errors.rejectValue("startPeriod", "", "existing (id=" + ap.getId() + ") price between startPeriod and endPeriod");
                break;
            }
        }
    }
}
