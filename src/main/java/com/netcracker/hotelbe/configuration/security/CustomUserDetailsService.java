package com.netcracker.hotelbe.configuration.security;

import com.netcracker.hotelbe.entity.Staff;
import com.netcracker.hotelbe.entity.User;
import com.netcracker.hotelbe.entity.enums.UserRole;
import com.netcracker.hotelbe.repository.StaffRepository;
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
        User user = userRepository.findByLogin(s).orElseThrow(
                () -> new UsernameNotFoundException("User with login " + s +" not found")
        );
        Staff staff = user.getStaff();
        if(staff != null){
            if(!staff.isActive()){
                user.setUserRole(UserRole.Client);
            }
        }
        return new CustomUserDetails(user);
    }

}
