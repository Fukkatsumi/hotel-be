package com.netcracker.hotelbe.service.validation;

import com.netcracker.hotelbe.entity.ApartmentClass;
import com.netcracker.hotelbe.entity.ApartmentClassCustom;
import com.netcracker.hotelbe.entity.Booking;
import com.netcracker.hotelbe.service.*;
import com.netcracker.hotelbe.utils.enums.MathOperation;
import com.netcracker.hotelbe.utils.enums.UnitOfTime;
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

    @Autowired
    private ApartmentClassService apartmentClassService;

    @Autowired
    private UserService userService;

    @Autowired
    private ApartmentService apartmentService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private SecurityService securityService;

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
        Timestamp createdDate = entityService.correctingTimestamp(booking.getCreatedDate(), MathOperation.MINUS, UnitOfTime.HOUR, 2);

        if (createdDate == null || createdDate.compareTo(currentTime) >= 0) {
            booking.setCreatedDate(currentTime);
        } else {
            booking.setCreatedDate(createdDate);
        }

        if (booking.getEndDate().compareTo(booking.getStartDate()) < 0) {
            errors.rejectValue("endDate", "", "End date cant be before start date");
        }

        if (booking.getUser() != null) {
            if (securityService.isManagerOrAdmin()) {
                booking.setUser(userService.findById(booking.getUser().getId()));
            } else {
                booking.setUser(userService.findByLogin(securityService.getCurrentUsername()));
            }
        }

        if (booking.getApartment() != null) {
            booking.setApartment(apartmentService.findById(booking.getApartment().getId()));
        }

        if (booking.getApartmentClass() == null) {
            errors.rejectValue("apartmentClass", "", "Apartment class cannot be empty");
        } else {
            booking.setApartmentClass(apartmentClassService.findById(booking.getApartmentClass().getId()));
            List<ApartmentClassCustom> apartmentClassCustomList = bookingService.findFreeApartments(booking.getStartDate().toString(), booking.getEndDate().toString());
            if (apartmentClassCustomList == null) {
                errors.rejectValue("startDate", "endDate", "Free apartments on these dates didn't find");
            } else {
                for (ApartmentClassCustom apartmentClassCustom :
                        apartmentClassCustomList) {
                    if (apartmentClassCustom.getApartmentClass().getId().equals(booking.getApartmentClass().getId())) {
                        if (apartmentClassCustom.getCountOfApartments() == 0) {
                            errors.rejectValue("apartmentClass", "", "Free apartment doesn't exist in this apartment Class");
                        } else {
                            booking.setTotalPrice(apartmentClassCustom.getApartmentPriceOnDates());
                        }
                    }
                }
            }
        }

    }
}
