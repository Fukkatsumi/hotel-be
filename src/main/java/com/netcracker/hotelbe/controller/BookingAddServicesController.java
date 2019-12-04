package com.netcracker.hotelbe.controller;


import com.netcracker.hotelbe.entity.BookingAddServices;
import com.netcracker.hotelbe.service.BookingAddServicesService;
import com.netcracker.hotelbe.utils.CustomEntityLogMessage;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("booking-add-services")
public class BookingAddServicesController {
    private static Logger logger = LogManager.getLogger(BookingAddServicesController.class);
    private final static String ENTITY_NAME = BookingAddServices.class.getSimpleName();
    private final static String BOOKING_ADD_BY_ID_NOT_FOUND = "BookingAddService by id: %d not found!";

    @Autowired
    BookingAddServicesService bookingAddServicesService;

    @GetMapping
    public ResponseEntity<List<BookingAddServices>> getAllBookingAddServices(){
        logger.info(String.format(CustomEntityLogMessage.REQUEST_FOR_GET_ALL_ENTITY, ENTITY_NAME));
        return new ResponseEntity<>(bookingAddServicesService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingAddServices> getBookingAddServiceById(@PathVariable Long id){
        logger.info(String.format(CustomEntityLogMessage.REQUEST_FOR_GET_ENTITY_BY_ID, ENTITY_NAME, id));
        return new ResponseEntity<>(bookingAddServicesService.findById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Long> addBookingAddService(@RequestBody @Valid BookingAddServices bookingAddServices){
        logger.info(String.format(CustomEntityLogMessage.REQUEST_FOR_CREATE_ENTITY, ENTITY_NAME));
        return new ResponseEntity<>(bookingAddServicesService.save(bookingAddServices).getId(), HttpStatus.OK);
    }


    @PutMapping
    public ResponseEntity<Long> updateBookingAddService(@RequestBody @Valid BookingAddServices bookingAddServices){
        logger.info(String.format(CustomEntityLogMessage.REQUEST_FOR_UPDATE_ENTITY_BY_ID, ENTITY_NAME, bookingAddServices.getId()));
        return new ResponseEntity<>(bookingAddServicesService.save(bookingAddServices).getId(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteBookingAddService(@PathVariable Long id){
        logger.info(String.format(CustomEntityLogMessage.REQUEST_FOR_DELETE_ENTITY_BY_ID, ENTITY_NAME, id));
        Long delete = bookingAddServicesService.deleteById(id);
        if (delete != 0) {
            return new ResponseEntity(HttpStatus.OK);
        } else {
            logger.warn(String.format(BOOKING_ADD_BY_ID_NOT_FOUND, id));
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

}
