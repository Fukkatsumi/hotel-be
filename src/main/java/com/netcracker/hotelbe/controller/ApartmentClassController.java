package com.netcracker.hotelbe.controller;


import com.netcracker.hotelbe.entity.ApartmentClass;
import com.netcracker.hotelbe.service.ApartmentClassService;
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
@RequestMapping("/apartmentsClasses")
public class ApartmentClassController {
    private static Logger logger = LogManager.getLogger(ApartmentClassController.class);
    private final static String ENTITY_NAME = ApartmentClass.class.getSimpleName();

    @Autowired
    private ApartmentClassService apartmentClassService;

    @GetMapping
    public ResponseEntity<List<ApartmentClass>> getAll() {
        logger.info(String.format(CustomEntityLogMessage.REQUEST_FOR_GET_ALL_ENTITY, ENTITY_NAME));

        return new ResponseEntity<>(apartmentClassService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApartmentClass> getById(@PathVariable("id") final Long id) {
        logger.info(String.format(CustomEntityLogMessage.REQUEST_FOR_GET_ENTITY_BY_ID, ENTITY_NAME, id));

        return new ResponseEntity<>(apartmentClassService.findById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody @Valid ApartmentClass apartmentClass) {
        logger.info(String.format(CustomEntityLogMessage.REQUEST_FOR_CREATE_ENTITY, ENTITY_NAME));

        return new ResponseEntity<>(apartmentClassService.save(apartmentClass),
                HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> update(@RequestBody @Valid ApartmentClass apartmentClass, @PathVariable Long id) {
        logger.info(String.format(CustomEntityLogMessage.REQUEST_FOR_UPDATE_ENTITY_BY_ID, ENTITY_NAME, apartmentClass.getId()));

        return new ResponseEntity<>(apartmentClassService.update(apartmentClass, id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteById(@PathVariable("id") final Long id) {
        logger.info(String.format(CustomEntityLogMessage.REQUEST_FOR_DELETE_ENTITY_BY_ID, ENTITY_NAME, id));

        apartmentClassService.deleteById(id);

        return new ResponseEntity(HttpStatus.OK);
    }

}
