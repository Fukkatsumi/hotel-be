package com.netcracker.hotelbe.controller;

import com.netcracker.hotelbe.entity.ApartmentPrice;
import com.netcracker.hotelbe.service.ApartmentPriceService;
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
@RequestMapping("apartmentPrices")
public class ApartmentPriceController {

    @Autowired
    private ApartmentPriceService apartmentPriceService;

    @GetMapping
    public ResponseEntity<List<ApartmentPrice>> getAll(@RequestParam Map<String,String> allParams) {
        return new ResponseEntity<>(apartmentPriceService.getAllByParams(allParams), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApartmentPrice> getById(@PathVariable("id") final Long id) {
        return new ResponseEntity<>(apartmentPriceService.findById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ApartmentPrice> add(@RequestBody @Valid ApartmentPrice apartmentPrice, BindingResult bindingResult) throws MethodArgumentNotValidException {
        apartmentPriceService.validate(apartmentPrice, bindingResult);

        try {
            return new ResponseEntity<>(apartmentPriceService.save(apartmentPrice), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return RuntimeExceptionHandler.handlePSQLException(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApartmentPrice> update(@RequestBody @Valid ApartmentPrice apartmentPrice, @PathVariable("id") Long id, BindingResult bindingResult) throws MethodArgumentNotValidException {
        apartmentPriceService.validate(apartmentPrice, bindingResult);

        try {
            return new ResponseEntity<>(apartmentPriceService.update(apartmentPrice, id), HttpStatus.OK);
        } catch (RuntimeException e) {
            return RuntimeExceptionHandler.handlePSQLException(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteById(@PathVariable("id") final Long id) {
        try {
            apartmentPriceService.deleteById(id);
        } catch (RuntimeException e) {
            return RuntimeExceptionHandler.handlePSQLException(e);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public  ResponseEntity<ApartmentPrice> patchById(@PathVariable("id") final Long id, @RequestBody Map<String, Object> updates) {
        return new ResponseEntity<>(apartmentPriceService.patch(id, updates), HttpStatus.OK);
    }

}
