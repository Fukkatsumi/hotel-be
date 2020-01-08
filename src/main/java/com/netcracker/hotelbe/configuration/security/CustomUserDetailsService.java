package com.netcracker.hotelbe.configuration.security;

import com.netcracker.hotelbe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return new CustomUserDetails(userRepository.findByLogin(s).orElseThrow(
                () -> new UsernameNotFoundException("User with login " + s +" not found")
        ));
    }

}
