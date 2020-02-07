package com.netcracker.hotelbe.controller;


import com.netcracker.hotelbe.entity.User;
import com.netcracker.hotelbe.service.UserService;
import com.netcracker.hotelbe.utils.RuntimeExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(@RequestParam Map<String, String> allParams) {
        return new ResponseEntity<>(userService.getAllByParams(allParams), HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return new ResponseEntity<>(userService.findById(id), HttpStatus.OK);
    }

    @GetMapping(params = "login")
    public ResponseEntity<User> getUserByLogin(@RequestParam String login) {
        return new ResponseEntity<>(userService.findByLogin(login), HttpStatus.OK);
    }

    @GetMapping("/current")
    public ResponseEntity<User> getCurrentUser(Authentication authentication){
        String username = authentication.getName();
        if(username != null){
            return new ResponseEntity<>(userService.findByLogin(username), HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
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

    @PatchMapping("/{id}")
    public ResponseEntity<User> patchById(@PathVariable("id") final Long id, @RequestBody Map<String, Object> updates) {
        return new ResponseEntity<>(userService.patch(id, updates), HttpStatus.OK);
    }

}
