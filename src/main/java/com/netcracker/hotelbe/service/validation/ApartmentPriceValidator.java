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
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ApartmentPriceValidator implements Validator {

    private final static String DATE_BETWEEN_EXISTING = "%s between existing (id=%s) startPeriod(%s) and endPeriod(%s)";

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

        List<ApartmentPrice> apartmentPrices = apartmentPriceService.findAll(values);

        for (ApartmentPrice apartmentPriceTemp : apartmentPrices) {
            LocalDate localStartPeriod = startPeriod.toLocalDate();
            LocalDate localEndPeriod = endPeriod.toLocalDate();

            LocalDate start = apartmentPriceTemp.getStartPeriod().toLocalDate();
            LocalDate end = apartmentPriceTemp.getEndPeriod().toLocalDate();
            if (localStartPeriod.compareTo(start) >= 0
                    && localStartPeriod.compareTo(end) <= 0) {
                errors.rejectValue("startPeriod", "", String.format(DATE_BETWEEN_EXISTING, "startPeriod", apartmentPriceTemp.getId(), start, end));
                break;
            }
            if (localEndPeriod.compareTo(start) >= 0
                    && localEndPeriod.compareTo(end) <= 0) {
                errors.rejectValue("startPeriod", "", String.format(DATE_BETWEEN_EXISTING, "endPeriod", apartmentPriceTemp.getId(), start, end));
                break;
            }
            if (localStartPeriod.compareTo(start) <= 0
                    && localEndPeriod.compareTo(end) >= 0) {
                errors.rejectValue("startPeriod", "", "existing (id=" + apartmentPriceTemp.getId() + ") price between startPeriod and endPeriod");
                break;
            }
        }
    }
}
