package com.netcracker.hotelbe.controller;

import com.netcracker.hotelbe.entity.Apartment;
import com.netcracker.hotelbe.entity.ApartmentClassCustom;
import com.netcracker.hotelbe.entity.Booking;
import com.netcracker.hotelbe.entity.BookingAddServicesCustom;
import com.netcracker.hotelbe.service.BookingAddServicesShipService;
import com.netcracker.hotelbe.service.BookingService;
import com.netcracker.hotelbe.utils.RuntimeExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingAddServicesShipService bookingAddServicesShipService;

    @GetMapping
    public ResponseEntity<List<Booking>> getAll(@RequestParam Map<String, String> allParams) {
        return new ResponseEntity<>(bookingService.getAllByParams(allParams), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Booking> create(@RequestBody @Valid Booking booking, BindingResult bindingResult) throws MethodArgumentNotValidException {
        System.out.println("got booking");
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
        return new ResponseEntity<>(bookingService.getById(id), HttpStatus.OK);
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

    @DeleteMapping("/{id}/cascade")
    public ResponseEntity cascadeDeleteById(@PathVariable("id") Long id){
        bookingService.cascadeDeleteById(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/find")
    public ResponseEntity<List<ApartmentClassCustom>> findFreeApartments(@RequestParam String startDate, @RequestParam String endDate) {
        return new ResponseEntity<>(bookingService.findFreeApartments(startDate, endDate), HttpStatus.OK);
    }

    @GetMapping("/findList")
    public ResponseEntity<List<Apartment>> findFreeApartments(@RequestParam String startDate, @RequestParam String endDate, @RequestParam String apartmentClass) {
        return new ResponseEntity<>(bookingService.findFreeApartmentsForApartmentClass(startDate, endDate, Long.parseLong(apartmentClass)), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Booking> patchById(@PathVariable("id") final Long id, @RequestBody Map<String, Object> updates) {
        return new ResponseEntity<>(bookingService.patch(id, updates), HttpStatus.OK);
    }

    @PostMapping("/{id}/services")
    public ResponseEntity<Long> addService(@PathVariable("id") Long id, @RequestBody @Valid Map<String, Long> values, BindingResult bindingResult) throws MethodArgumentNotValidException {
        bookingService.validate(values, bindingResult);
        try {
            return new ResponseEntity<>(bookingService.addService(id, values), HttpStatus.OK);
        } catch (RuntimeException e) {
            return RuntimeExceptionHandler.handlePSQLException(e);
        }
    }

    @PatchMapping("/{id}/services/recalculation")
    public ResponseEntity<Integer> recalculationBookingPrice(@PathVariable("id") Long id){
        return new ResponseEntity<>(bookingService.recalculatePrice(id), HttpStatus.OK);
    }
  
    @PostMapping("/{id}/servicesList")
    public ResponseEntity<List<Long>> addMultipleService(@PathVariable("id") Long id, @RequestBody List<Map<String, Long>> values, BindingResult bindingResult) throws MethodArgumentNotValidException {
        bookingService.validate(values, bindingResult);
        try {
            return new ResponseEntity<>(bookingService.addService(id, values), HttpStatus.OK);
        } catch (RuntimeException e) {
            return RuntimeExceptionHandler.handlePSQLException(e);
        }
    }

    @GetMapping("/{id}/services")
    public ResponseEntity<List<BookingAddServicesCustom>> getServices(@PathVariable("id") Long id) {
        return new ResponseEntity<>(bookingService.getServices(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}/services/{serviceId}")
    public ResponseEntity deleteService(@PathVariable Long id, @PathVariable Long serviceId) throws Throwable {
        bookingService.deleteService(id, serviceId);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/{id}/services")
    public ResponseEntity deleteMultipleServices(@PathVariable Long id){
        bookingAddServicesShipService.deleteServicesByBookingId(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
