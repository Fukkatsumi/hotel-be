package com.netcracker.hotelbe.service;

import com.netcracker.hotelbe.entity.Apartment;
import com.netcracker.hotelbe.entity.ApartmentClass;
import com.netcracker.hotelbe.entity.Booking;
import com.netcracker.hotelbe.entity.User;
import com.netcracker.hotelbe.repository.BookingRepository;
import com.netcracker.hotelbe.utils.CustomEntityLogMessage;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.NoSuchElementException;

import static org.hibernate.id.IdentifierGenerator.ENTITY_NAME;

@Service
public class BookingService {
    private static Logger logger = LogManager.getLogger(ApartmentClassService.class);
    private final static String ENTITY_NAME = Booking.class.getSimpleName();
    @Autowired
    BookingRepository bookingRepository;


    public List<Booking> getAll() {
        return bookingRepository.findAll();
    }

    public Long save(final Booking booking) {
        logger.trace(String.format(CustomEntityLogMessage.SAVE_ENTITY, ENTITY_NAME));

        final Booking save = bookingRepository.save(booking);
        final Long id = save.getId();
        logger.trace(String.format(CustomEntityLogMessage.SAVE_ENTITY_WITH_ID, ENTITY_NAME, id));

        return id;
    }

    public Booking findById(Long id) {
        try {
            return bookingRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    public Long update(Booking booking) {
        logger.trace(String.format(CustomEntityLogMessage.UPDATE_ENTITY, ENTITY_NAME));

        final Long bookingId = booking.getId();

        Booking update = bookingRepository.findById(bookingId).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(bookingId))
        );
        logger.trace(String.format(CustomEntityLogMessage.FOUND_ENTITY_WITH_ID, ENTITY_NAME, bookingId));

        update.setId(booking.getId());
        update.setBookingStatus(booking.getBookingStatus());
        update.setComment(booking.getComment());
        update.setCreatedDate(booking.getCreatedDate());
        update.setStartDate(booking.getStartDate());
        update.setEndDate(booking.getEndDate());
        update.setReview(booking.getReview());
        update.setTotalPrice(booking.getTotalPrice());
        update.setApartmentClass(booking.getApartmentClass());
        update.setApartment(booking.getApartment());
        update.setUser(booking.getUser());
        update = bookingRepository.save(update);
        logger.trace(String.format(CustomEntityLogMessage.UPDATED_ENTITY_SAVED, ENTITY_NAME));

        return update.getId();
    }

    public boolean deleteById(Long id) {
        Booking delete;
        try {
            delete = bookingRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            return false;
        }

        bookingRepository.delete(delete);
        return true;
    }
}
