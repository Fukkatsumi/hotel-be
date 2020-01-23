package com.netcracker.hotelbe.controller;

import com.netcracker.hotelbe.entity.Email;
import com.netcracker.hotelbe.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/user/{id}/credential")
    public ResponseEntity sendCredentialToUser(@PathVariable("id") Long id){
        emailService.sendCredentialToUser(id);

        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/user/{id}")
    public ResponseEntity sendEmailToUser(@PathVariable("id") Long id, @RequestBody Email email){
        emailService.sendToUser(email, id);

        return new ResponseEntity(HttpStatus.OK);
    }
}
