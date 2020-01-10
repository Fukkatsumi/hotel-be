package com.netcracker.hotelbe.service;

import com.netcracker.hotelbe.entity.User;
import com.netcracker.hotelbe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> findAll(){
        return userRepository.findAll();
    }

    public User findById(Long id){
        return userRepository.findById(id).orElseThrow(
                ()->new EntityNotFoundException("No entity with id=" + id + " found")
        );
    }

    public User findByLogin(String login){
        return userRepository.findByLogin(login).orElseThrow(
                ()->new EntityNotFoundException("No entity with login=" + login + " found")
        );
    }

    public void deleteById(Long id){
        if (!userRepository.findById(id).isPresent()){
            throw new EntityNotFoundException("No entity with id=" + id + " found");
        }
        userRepository.deleteById(id);
    }

    public User save(User user){
        return userRepository.save(user);
    }

}
