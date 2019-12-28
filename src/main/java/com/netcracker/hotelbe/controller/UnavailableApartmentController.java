package com.netcracker.hotelbe.controller;

import com.netcracker.hotelbe.entity.UnavailableApartment;
import com.netcracker.hotelbe.service.UnavailableApartmentService;
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
@RequestMapping("unavailableApartments")
public class UnavailableApartmentController {

    @Autowired
    private UnavailableApartmentService unavailableApartmentService;

    @GetMapping
    public ResponseEntity<List<UnavailableApartment>> getAll(@RequestParam Map<String,String> allParams) {
        return new ResponseEntity<>(unavailableApartmentService.getAllByParams(allParams), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UnavailableApartment> getById(@PathVariable("id") final Long id) {
        return new ResponseEntity<>(unavailableApartmentService.findById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UnavailableApartment> add(@RequestBody @Valid UnavailableApartment unavailableApartment, BindingResult bindingResult) throws MethodArgumentNotValidException {
        unavailableApartmentService.validate(unavailableApartment, bindingResult);

        try {
            return new ResponseEntity<>(unavailableApartmentService.save(unavailableApartment), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return RuntimeExceptionHandler.handlePSQLException(e);
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<UnavailableApartment> update(@RequestBody @Valid UnavailableApartment unavailableApartment, @PathVariable("id") final Long id, BindingResult bindingResult) throws MethodArgumentNotValidException {
        unavailableApartmentService.validate(unavailableApartment, bindingResult);

        try {
            return new ResponseEntity<>(unavailableApartmentService.update(unavailableApartment, id), HttpStatus.OK);
        } catch (RuntimeException e) {
            return RuntimeExceptionHandler.handlePSQLException(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteById(@PathVariable("id") final Long id) {
        try {
            unavailableApartmentService.deleteById(id);
        } catch (RuntimeException e) {
            return RuntimeExceptionHandler.handlePSQLException(e);
        }
        return new ResponseEntity(HttpStatus.OK);
    }
}
