package com.netcracker.hotelbe.service;

import com.netcracker.hotelbe.entity.*;
import com.netcracker.hotelbe.repository.BookingRepository;
import com.netcracker.hotelbe.service.filter.FilterService;
import com.netcracker.hotelbe.utils.LoggingManager;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.persistence.EntityNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class BookingService {

    private static final Logger LOG = LogManager.getLogger(LoggingManager.class);

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    ApartmentClassService apartmentClassService;

    @Autowired
    ApartmentService apartmentService;

    @Autowired
    UserService userService;

    @Autowired
    UnavailableApartmentService unavailableApartmentService;

    @Autowired
    private FilterService filterService;

    @Autowired
    private EntityService entityService;

    @Autowired
    @Qualifier("bookingValidator")
    private Validator bookingValidator;

    public List<Booking> getAll() {
        return bookingRepository.findAll();
    }

    public Booking save(final Booking booking) {
        final ApartmentClass apartmentClass = apartmentClassService.findById(booking.getApartmentClass().getId());
        booking.setApartmentClass(apartmentClass);
        final User user = userService.findById(booking.getUser().getId());
        booking.setUser(user);
        return bookingRepository.save(booking);
    }

    public Booking findById(Long id) {
        return bookingRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );
    }

    public List<Booking> getAllByParams(Map<String, String> allParams) {
        if(allParams.size()!=0) {
            return bookingRepository.findAll(filterService.fillFilter(allParams, Booking.class));
        } else {
            return bookingRepository.findAll();
        }
    }

    public Booking update(Booking booking, Long id) {

        bookingRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );

        ApartmentClass apartmentClass = apartmentClassService.findById(booking.getApartmentClass().getId());

        User user = userService.findById(booking.getUser().getId());

        booking.setApartmentClass(apartmentClass);
        booking.setUser(user);
        booking.setId(id);

        return bookingRepository.save(booking);
    }

    public void deleteById(Long id) {
        Booking delete = bookingRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );

        bookingRepository.delete(delete);
    }

    public Booking patch(Long id, Map<String, Object> updates) {
        Booking booking = bookingRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );

        return bookingRepository.save((Booking) entityService.fillFields(updates, booking));
    }

    public List<ApartmentClassCustom> findFreeApartments(String startDateStr, String endDateStr) {
        List<Booking> bookingList = getAll();
        List<UnavailableApartment> unavailableApartmentList = unavailableApartmentService.getAll();
        List<Apartment> apartmentList = apartmentService.getAll();
        Date startDate = toDate(startDateStr);
        Date endDate = toDate(endDateStr);
        if (isValidDates(startDate, endDate)) return null;
        for (UnavailableApartment unavailableApartment:
             unavailableApartmentList) {
            if ((startDate.compareTo(unavailableApartment.getStartDate()) >= 0
                    && (endDate.compareTo(unavailableApartment.getEndDate()) <= 0))
                    || ((startDate.compareTo(unavailableApartment.getStartDate()) < 0)
                    && (endDate.compareTo(unavailableApartment.getEndDate()) <= 0)
                    &&(endDate.compareTo(unavailableApartment.getStartDate()) >= 0))
                    ||  ((startDate.compareTo(unavailableApartment.getStartDate()) >= 0)
                    && (startDate.compareTo(unavailableApartment.getEndDate()) <= 0)
                    && (endDate.compareTo(unavailableApartment.getEndDate()) > 0))
                    || ((startDate.compareTo(unavailableApartment.getStartDate()) < 0 )
                    && (endDate.compareTo(unavailableApartment.getEndDate()) > 0)))  {
                apartmentList.remove(unavailableApartment.getApartment());
            }
        }
        for (Booking booking:
                bookingList) {
            if ((startDate.compareTo(booking.getStartDate()) >= 0
                    && (endDate.compareTo(booking.getEndDate()) <= 0))
                    || ((startDate.compareTo(booking.getStartDate()) < 0)
                    && (endDate.compareTo(booking.getEndDate()) <= 0)
                    &&(endDate.compareTo(booking.getStartDate()) >= 0))
                    ||  ((startDate.compareTo(booking.getStartDate()) >= 0)
                    && (startDate.compareTo(booking.getEndDate()) <= 0)
                    && (endDate.compareTo(booking.getEndDate()) > 0))
                    || ((startDate.compareTo(booking.getStartDate()) < 0 )
                    && (endDate.compareTo(booking.getEndDate()) > 0))) {
                removeApartment(apartmentList, booking);
            }
        }
        return toApartmentClassCustom(apartmentList);
    }

    private void removeApartment(List<Apartment> apartmentList, Booking booking) {
        ApartmentClass apartmentClass;
        if (booking.getApartment() == null) {
            apartmentClass = booking.getApartmentClass();
            for (Apartment apartment :
                    apartmentList) {
                if (apartment.getApartmentClass() == apartmentClass) {
                    apartmentList.remove(apartment);
                    break;
                }
            }
        }
        apartmentList.remove(booking.getApartment());
    }

    private Date toDate(String strDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            java.util.Date date = simpleDateFormat.parse(strDate);
            long dateLong = date.getTime();
            return new Date(dateLong);
        } catch (ParseException e) {
            LOG.error(e.getMessage());
        }
        return null;
    }

    private boolean isValidDates(Date startDate, Date endDate) {
        return endDate == null || startDate == null || endDate.compareTo(startDate) < 0;
    }

    public void validate(final Booking booking, BindingResult bindingResult) throws MethodArgumentNotValidException {
        bookingValidator.validate(booking, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new MethodArgumentNotValidException(null, bindingResult);
        }
    }

    private List<ApartmentClassCustom> toApartmentClassCustom(List<Apartment> apartmentList) {
        List<ApartmentClassCustom> apartmentClassCustomsList = new ArrayList<>();
        for (ApartmentClass apartmentClass:
             apartmentClassService.findAll()) {
            ApartmentClassCustom apartmentClassCustomTemp = new ApartmentClassCustom(apartmentClass);
            apartmentClassCustomsList.add(apartmentClassCustomTemp);
        }
        for (Apartment apartment:
             apartmentList) {
            for (ApartmentClassCustom apClassCustom:
                 apartmentClassCustomsList) {
                if (apClassCustom.getApartmentClass().getId().equals(apartment.getApartmentClass().getId())){
                    apClassCustom.setCountOfApartments(apClassCustom.getCountOfApartments()+1);
                }
            }
        }
        return apartmentClassCustomsList;
    }
}
