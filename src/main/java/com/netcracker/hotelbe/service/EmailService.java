package com.netcracker.hotelbe.service;

import com.netcracker.hotelbe.entity.Email;
import com.netcracker.hotelbe.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static String USER_CREDENTIAL = "You have successfully registered!\n\nHi, %s!\nYour credential\nLogin: %s\nPassword: %s";

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private UserService userService;

    public void sendToUser(Email email, Long userId) {
        User user = userService.findById(userId);

        send(email, user.getEmail());

    }


    public void sendCredentialToUser(Long id) {
        User user = userService.findById(id);

        Email email = new Email();
        email.setSubject("Hotel: your credential");
        email.setText(String.format(USER_CREDENTIAL, user.getFirstname(), user.getLogin(), user.getPassword()));

        send(email, user.getEmail());
    }


    private void send(Email email, String address) {
        SimpleMailMessage smm = new SimpleMailMessage();
        smm.setTo(address);
        smm.setSubject(email.getSubject());
        smm.setText(email.getText());

        javaMailSender.send(smm);
    }
}
