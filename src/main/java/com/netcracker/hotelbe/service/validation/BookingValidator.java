package com.netcracker.hotelbe.service.validation;

import com.netcracker.hotelbe.entity.ApartmentClassCustom;
import com.netcracker.hotelbe.entity.Booking;
import com.netcracker.hotelbe.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.validation.ConstraintViolation;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Service
public class BookingValidator implements Validator {

    @Autowired
    private javax.validation.Validator validator;

    @Autowired
    private BookingService bookingService;

    @Override
    public boolean supports(Class<?> aClass) {
        return Booking.class.equals(aClass);
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

        Booking booking = (Booking) o;

        Timestamp currentTime = new Timestamp(System.currentTimeMillis() - 120000);
        booking.setCreatedDate(currentTime);

        if (booking.getEndDate().compareTo(booking.getStartDate()) < 0){
            errors.rejectValue("endDate", "", "End date cant be before start date");
        }
        List<ApartmentClassCustom> apartmentClassCustomList = bookingService.findFreeApartments(booking.getStartDate().toString(), booking.getEndDate().toString());
        if (apartmentClassCustomList == null) {
            errors.rejectValue("startDate", "endDate", "Free apartments on these dates didn't find");
        } else {
            for (ApartmentClassCustom apartmentClassCustom :
                    apartmentClassCustomList) {
                if (apartmentClassCustom.getApartmentClass().getId().equals(booking.getApartmentClass().getId())) {
                    if (apartmentClassCustom.getCountOfApartments() == 0) {
                        errors.rejectValue("apartmentClass", "", "Free apartment doesn't exist in this apartment Class");
                    }
                }
            }
        }

    }
}
