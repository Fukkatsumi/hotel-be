package com.netcracker.hotelbe.controller;


import com.netcracker.hotelbe.entity.BookingAddServices;
import com.netcracker.hotelbe.service.BookingAddServicesService;
import com.netcracker.hotelbe.utils.RuntimeExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("bookingAddServices")
public class BookingAddServicesController {

    @Autowired
    BookingAddServicesService bookingAddServicesService;

    @GetMapping
    public ResponseEntity<List<BookingAddServices>> getAllBookingAddServices() {
        return new ResponseEntity<>(bookingAddServicesService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingAddServices> getBookingAddServiceById(@PathVariable Long id) {
        return new ResponseEntity<>(bookingAddServicesService.findById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Long> addBookingAddService(@RequestBody @Valid BookingAddServices bookingAddServices) {
        try {
            return new ResponseEntity<>(bookingAddServicesService.save(bookingAddServices).getId(), HttpStatus.OK);
        } catch (RuntimeException e) {
            return RuntimeExceptionHandler.handlePSQLException(e);
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<BookingAddServices> updateBookingAddService(@RequestBody @Valid BookingAddServices bookingAddServices, @PathVariable Long id) {
        try {
            return new ResponseEntity<>(bookingAddServicesService.update(bookingAddServices, id), HttpStatus.OK);
        } catch (RuntimeException e) {
            return RuntimeExceptionHandler.handlePSQLException(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteBookingAddService(@PathVariable Long id) {
        bookingAddServicesService.deleteById(id);
        return new ResponseEntity(HttpStatus.OK);
    }

}
