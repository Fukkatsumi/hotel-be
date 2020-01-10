package com.netcracker.hotelbe.controller;

import com.netcracker.hotelbe.entity.ApartmentClassCustom;
import com.netcracker.hotelbe.entity.Booking;
import com.netcracker.hotelbe.entity.BookingAddServicesCustom;
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
import java.util.Map;


@RestController
@RequestMapping("bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @GetMapping
    public ResponseEntity<List<Booking>> getAll(@RequestParam Map<String, String> allParams) {
        return new ResponseEntity<>(bookingService.getAllByParams(allParams), HttpStatus.OK);
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
    public ResponseEntity<List<ApartmentClassCustom>> findFreeApartments(@RequestParam String startDate, @RequestParam String endDate) {
        return new ResponseEntity<>(bookingService.findFreeApartments(startDate, endDate), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Booking> patchById(@PathVariable("id") final Long id, @RequestBody Map<String, Object> updates) {
        return new ResponseEntity<>(bookingService.patch(id, updates), HttpStatus.OK);
    }

    @PostMapping("/{id}/services")
    public ResponseEntity<Long> addService(@PathVariable("id") Long id, @RequestBody Map<String, Long> servicesId) {
        try {
            return new ResponseEntity<>(bookingService.addService(id, servicesId), HttpStatus.OK);
        } catch (RuntimeException e) {
            return RuntimeExceptionHandler.handlePSQLException(e);
        }
    }

    @GetMapping("/{id}/services")
    public ResponseEntity<List<BookingAddServicesCustom>> getServices(@PathVariable("id") Long id) {
        return new ResponseEntity<>(bookingService.getServices(id), HttpStatus.OK);
    }
}
