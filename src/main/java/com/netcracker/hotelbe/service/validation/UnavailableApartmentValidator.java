package com.netcracker.hotelbe.service.validation;

import com.netcracker.hotelbe.entity.ApartmentPrice;
import com.netcracker.hotelbe.entity.UnavailableApartment;
import com.netcracker.hotelbe.service.UnavailableApartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.validation.ConstraintViolation;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class UnavailableApartmentValidator implements Validator {

    private final static String DATE_BETWEEN_EXISTING = "%s between existing (id=%s) startDate(%s) and endDate(%s)";

    @Autowired
    private javax.validation.Validator validator;

    @Autowired
    private UnavailableApartmentService unavailableApartmentService;

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
            errors.rejectValue("startDate", "", "Start date cant be before current date ");
        }
        if (unavailableApartment.getEndDate().compareTo(currentTime) < 0) {
            errors.rejectValue("endDate", "", "End date cant be before current date ");
        }
        if (unavailableApartment.getEndDate().compareTo(unavailableApartment.getStartDate()) < 0) {
            errors.rejectValue("endDate", "", "End date cant be before start date");
        }

        Map<String, String> values = new HashMap<>();
        values.put("apartment", String.valueOf(unavailableApartment.getApartment().getId()));

        List<UnavailableApartment> unavailableApartmentList = unavailableApartmentService.findAll(values);

        for (UnavailableApartment unavailableApartmentTemp : unavailableApartmentList) {
            LocalDate localStartPeriod = unavailableApartment.getStartDate().toLocalDate();
            LocalDate localEndPeriod = unavailableApartment.getEndDate().toLocalDate();

            LocalDate start = unavailableApartmentTemp.getStartDate().toLocalDate();
            LocalDate end = unavailableApartmentTemp.getEndDate().toLocalDate();
            if (localStartPeriod.compareTo(start) >= 0
                    && localStartPeriod.compareTo(end) <= 0) {
                errors.rejectValue("startDate", "", String.format(DATE_BETWEEN_EXISTING, "startDate", unavailableApartmentTemp.getId(), start, end));
                break;
            }
            if (localEndPeriod.compareTo(start) >= 0
                    && localEndPeriod.compareTo(end) <= 0) {
                errors.rejectValue("startDate", "", String.format(DATE_BETWEEN_EXISTING, "endDate", unavailableApartmentTemp.getId(), start, end));
                break;
            }
            if (localStartPeriod.compareTo(start) <= 0
                    && localEndPeriod.compareTo(end) >= 0) {
                errors.rejectValue("startDate", "", "existing (id=" + unavailableApartmentTemp.getId() +
                        ") unavailable apartment between startPeriod and endDate");
                break;
            }
        }
    }
}
