package com.netcracker.hotelbe.service;

import com.netcracker.hotelbe.entity.ApartmentClass;
import com.netcracker.hotelbe.entity.Booking;
import com.netcracker.hotelbe.entity.User;
import com.netcracker.hotelbe.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class BookingService {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    ApartmentClassService apartmentClassService;

    @Autowired
    ApartmentService apartmentService;

    @Autowired
    UserService userService;

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

    public ApartmentClass findFreeApartments(String arriveDate, String departureDate) {
        List<Booking> bookingList = getAll();
        List<Date> timestampList = new ArrayList<>();
        ApartmentClass apartmentClass = new ApartmentClass();
        for (Booking booking:
             bookingList) {
            apartmentClass = booking.getApartmentClass();
        }
        return apartmentClass;
    }
/*
    private static String decodeValue(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }
 */
}
