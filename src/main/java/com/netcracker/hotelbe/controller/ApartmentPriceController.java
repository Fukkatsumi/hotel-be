package com.netcracker.hotelbe.controller;

import com.netcracker.hotelbe.entity.ApartmentPrice;
import com.netcracker.hotelbe.service.ApartmentPriceService;
import com.netcracker.hotelbe.utils.CustomEntityLogMessage;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/apartmentPrices")
public class ApartmentPriceController {
    private static Logger logger = LogManager.getLogger(ApartmentPriceController.class);
    private final static String ENTITY_NAME = ApartmentPrice.class.getSimpleName();

    @Autowired
    private ApartmentPriceService apartmentPriceService;

    @GetMapping
    public ResponseEntity<List<ApartmentPrice>> getAll() {
        logger.info(String.format(CustomEntityLogMessage.REQUEST_FOR_GET_ALL_ENTITY, ENTITY_NAME));

        return new ResponseEntity<>(apartmentPriceService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApartmentPrice> getById(@PathVariable("id") final Long id) {
        logger.info(String.format(CustomEntityLogMessage.REQUEST_FOR_GET_ENTITY_BY_ID, ENTITY_NAME, id));

        return new ResponseEntity<>(apartmentPriceService.findById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody ApartmentPrice apartmentPrice) {
        logger.info(String.format(CustomEntityLogMessage.REQUEST_FOR_CREATE_ENTITY, ENTITY_NAME));

        return new ResponseEntity<>(apartmentPriceService.save(apartmentPrice), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> update(@RequestBody ApartmentPrice apartmentPrice, @PathVariable("id") Long id) {
        logger.info(String.format(CustomEntityLogMessage.REQUEST_FOR_UPDATE_ENTITY_BY_ID, ENTITY_NAME, apartmentPrice.getId()));

        return new ResponseEntity<>(apartmentPriceService.update(apartmentPrice, id), HttpStatus.OK);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteById(@PathVariable("id") final Long id) {
        logger.info(String.format(CustomEntityLogMessage.REQUEST_FOR_DELETE_ENTITY_BY_ID, ENTITY_NAME, id));

        apartmentPriceService.deleteById(id);

        return new ResponseEntity(HttpStatus.OK);
    }
}
