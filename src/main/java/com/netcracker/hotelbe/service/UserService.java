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
                ()->new EntityNotFoundException(String.valueOf(id))
        );
    }

    public void deleteById(Long id){
        if (!userRepository.findById(id).isPresent()){
            throw new EntityNotFoundException(String.valueOf(id));
        }
        userRepository.deleteById(id);
    }

    public User save(User user){
        return userRepository.save(user);
    }

    public User update(User user, Long id){
        userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );

        user.setId(id);

        return userRepository.save(user);
    }
}
