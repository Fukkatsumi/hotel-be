package com.netcracker.hotelbe.controller;


import com.netcracker.hotelbe.entity.User;
import com.netcracker.hotelbe.service.UserService;
import com.netcracker.hotelbe.utils.RuntimeExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return new ResponseEntity<>(userService.findById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Long> addUser(@RequestBody @Valid User user) {
        try {
            return new ResponseEntity<>(userService.save(user).getId(), HttpStatus.OK);
        } catch (RuntimeException e) {
            return RuntimeExceptionHandler.handlePSQLException(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateUser(@Valid @RequestBody User user, @PathVariable("id") Long id) {

        try {
            return new ResponseEntity<>(userService.update(user, id).getId(), HttpStatus.OK);
        } catch (RuntimeException e) {
            return RuntimeExceptionHandler.handlePSQLException(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return new ResponseEntity(HttpStatus.OK);

    }


}
