package com.netcracker.hotelbe.controller;

import com.netcracker.hotelbe.entity.Apartment;
import com.netcracker.hotelbe.entity.ApartmentClass;
import com.netcracker.hotelbe.entity.Booking;
import com.netcracker.hotelbe.service.BookingService;
import com.netcracker.hotelbe.utils.RuntimeExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @GetMapping
    public ResponseEntity<List<Booking>> getAll() {
        return new ResponseEntity<>(bookingService.getAll(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Booking> create(@RequestBody @Valid Booking booking, BindingResult bindingResult) throws MethodArgumentNotValidException {
        bookingService.validate(booking, bindingResult);
        try {
            return new ResponseEntity<>(bookingService.save(booking),
                    HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return RuntimeExceptionHandler.handlePSQLException(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getById(@PathVariable("id") final Long id) {
        return new ResponseEntity<>(bookingService.findById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Booking> update(@RequestBody @Valid Booking booking, @PathVariable("id") final Long id, BindingResult bindingResult) throws MethodArgumentNotValidException {
        bookingService.validate(booking, bindingResult);
        try {
            return new ResponseEntity<>(bookingService.update(booking, id), HttpStatus.OK);
        } catch (RuntimeException e) {
            return RuntimeExceptionHandler.handlePSQLException(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteById(@PathVariable Long id) {
        bookingService.deleteById(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/find")
    public ResponseEntity<Integer> findFreeApartments(@RequestParam String startDate, @RequestParam String endDate) {
        return new ResponseEntity<>(bookingService.findFreeApartments(startDate, endDate), HttpStatus.OK);
    }
}
