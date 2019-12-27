package com.netcracker.hotelbe.service;

import com.netcracker.hotelbe.entity.User;
import com.netcracker.hotelbe.repository.UserRepository;
import com.netcracker.hotelbe.service.filter.FilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FilterService filterService;

    public List<User> findAll(){
        return userRepository.findAll();
    }

    public List<User> getAllByParams(Map<String, String> allParams) {
        if(allParams.size()!=0) {
            return userRepository.findAll(filterService.fillFilter(allParams, User.class));
        } else {
            return userRepository.findAll();
        }
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
