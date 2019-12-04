package com.netcracker.hotelbe.controller;

import com.netcracker.hotelbe.entity.Booking;
import com.netcracker.hotelbe.service.BookingService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("booking")
public class BookingController {
    private final static Logger logger = Logger.getLogger(BookingController.class);
    private final static String BOOKING_BY_ID_NOT_FOUND = "Booking by id: %d not found!";

    @Autowired
    BookingService bookingService;

    @GetMapping("/all")
    public ResponseEntity getAll() {
        logger.info("Request for get all bookings");

        return new ResponseEntity(bookingService.getAll(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity create(@RequestBody @Valid Booking booking) {
        logger.info("Request for create booking");

        return new ResponseEntity(bookingService.save(booking),
                HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity getById(@PathVariable Long id) {
        logger.info("Request for get booking by id: " + id);

        Booking booking = bookingService.findById(id);
        if (booking != null) {
            return new ResponseEntity(booking, HttpStatus.OK);
        } else {
            logger.warn(String.format(BOOKING_BY_ID_NOT_FOUND, id));

            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping
    public ResponseEntity update(@RequestBody @Valid Booking booking) {
        logger.info("Request for update booking by id: " + booking.getId());

        Long update = bookingService.update(booking);

        if (update != 0) {
            return new ResponseEntity(HttpStatus.OK);
        } else {
            logger.warn("Booking by id: " + booking.getId());
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteById(@PathVariable Long id) {
        logger.info("Request for delete booking by id: " +id);

        boolean delete = bookingService.deleteById(id);
        if (delete) {
            return new ResponseEntity(HttpStatus.OK);
        } else {
            logger.warn(String.format(BOOKING_BY_ID_NOT_FOUND, id));
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }
}
