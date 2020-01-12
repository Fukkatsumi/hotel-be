package com.netcracker.hotelbe.controller;

import com.netcracker.hotelbe.entity.ApartmentClass;
import com.netcracker.hotelbe.entity.ApartmentPrice;
import com.netcracker.hotelbe.service.ApartmentClassService;
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
@RequestMapping("apartmentsClasses")
public class ApartmentClassController {

    @Autowired
    private ApartmentClassService apartmentClassService;

    @GetMapping()
    public ResponseEntity<List<ApartmentClass>> getAll(@RequestParam Map<String, String> allParams) {
        return new ResponseEntity<>(apartmentClassService.getAllByParams(allParams), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApartmentClass> getById(@PathVariable("id") final Long id) {
        return new ResponseEntity<>(apartmentClassService.findById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ApartmentClass> add(@RequestBody @Valid ApartmentClass apartmentClass, BindingResult bindingResult) throws MethodArgumentNotValidException {
        apartmentClassService.validate(apartmentClass, bindingResult);

        try {
            return new ResponseEntity<>(apartmentClassService.save(apartmentClass),
                    HttpStatus.OK);
        } catch (RuntimeException e) {
            return RuntimeExceptionHandler.handlePSQLException(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApartmentClass> update(@RequestBody @Valid ApartmentClass apartmentClass, @PathVariable Long id, BindingResult bindingResult) throws MethodArgumentNotValidException {
        apartmentClassService.validate(apartmentClass, bindingResult);

        try {
            return new ResponseEntity<>(apartmentClassService.update(apartmentClass, id), HttpStatus.OK);
        } catch (RuntimeException e) {
            return RuntimeExceptionHandler.handlePSQLException(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteById(@PathVariable("id") final Long id) {
        try {
            apartmentClassService.deleteById(id);
        } catch (RuntimeException e) {
            return RuntimeExceptionHandler.handlePSQLException(e);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApartmentClass> patchById(@PathVariable("id") final Long id, @RequestBody Map<String, Object> updates) {
        return new ResponseEntity<>(apartmentClassService.patch(id, updates), HttpStatus.OK);
    }

    @GetMapping("/{id}/prices")
    public ResponseEntity<List<ApartmentPrice>> getPrices(@PathVariable("id") final Long id) {
        return new ResponseEntity<>(apartmentClassService.getPrices(id), HttpStatus.OK);
    }
}
