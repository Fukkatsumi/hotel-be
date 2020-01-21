package com.netcracker.hotelbe.service;

import com.netcracker.hotelbe.entity.User;
import com.netcracker.hotelbe.repository.UserRepository;
import com.netcracker.hotelbe.service.filter.FilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.*;
import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FilterService filterService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JavaMailSender javaMailSender;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> getAllByParams(Map<String, String> allParams) {
        if (allParams.size() != 0) {
            return userRepository.findAll(filterService.fillFilter(allParams, User.class));
        } else {
            return userRepository.findAll();
        }
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );
    }

    public User findByLogin(String login) {
        return userRepository.findByLogin(login).orElseThrow(
                () -> new EntityNotFoundException("No entity with login=" + login + " found")
        );
    }

    public void deleteById(Long id) {
        if (!userRepository.findById(id).isPresent()) {
            throw new EntityNotFoundException(String.valueOf(id));
        }
        userRepository.deleteById(id);
    }

    public User patch(Long id, Map<String, Object> updates) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );

        return userRepository.save((User) entityService.fillFields(updates, user));
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User update(User user, Long id) {
        userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );

        user.setId(id);

        return userRepository.save(user);
    }
}
