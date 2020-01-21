package com.netcracker.hotelbe.service;

import com.netcracker.hotelbe.entity.Email;
import com.netcracker.hotelbe.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

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
        email.setSubject("Hotel: info about user: " + user.getId());
        email.setText("You have successfully registered!\n\n" +
                "Hi, " + user.getFirstname() + "!\n" +
                "Your credential\n" +
                "Login: " + user.getLogin() + "\n" +
                "Password: " + user.getPassword());

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
