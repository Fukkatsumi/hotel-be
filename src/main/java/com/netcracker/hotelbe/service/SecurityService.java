package com.netcracker.hotelbe.service;

import com.netcracker.hotelbe.entity.User;
import com.netcracker.hotelbe.entity.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    @Autowired
    private UserService userService;

    public boolean isManagerOrAdmin(){
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().toString().equals(UserRole.Administrator.name())
                || SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().toString().equals(UserRole.Manager.name());
    }

    public String getCurrentUsername(){
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
    }

    public User getCurrentUser() {
        String login = getCurrentUsername();
        return userService.findByLogin(login);
    }
}
