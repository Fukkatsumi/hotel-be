package com.netcracker.hotelbe.service;

import com.netcracker.hotelbe.entity.Staff;
import com.netcracker.hotelbe.repository.StaffRepository;
import com.netcracker.hotelbe.service.filter.FilterService;
import com.netcracker.hotelbe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Service
public class StaffService {

    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private FilterService filterService;

    @Autowired
    private EntityService entityService;

    public Staff findById(long id) {
        return staffRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );
    }

    public List<Staff> findAll() {
        return staffRepository.findAll();
    }

    public List<Staff> getAllByParams(Map<String, String> allParams) {
        if(allParams.size()!=0) {
            return staffRepository.findAll(filterService.fillFilter(allParams, Staff.class));
        } else {
            return staffRepository.findAll();
        }
    }

    public Staff save(Staff staff) {
        return staffRepository.save(staff);
    }

    public Staff update(Staff staff, Long id) {
        staffRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );

        staff.setUser(userService.findById(id));
        staff.setId(id);

        return staffRepository.save(staff);
    }

    public void deleteById(Long id) {
        if (!staffRepository.findById(id).isPresent()) {
            throw new EntityNotFoundException(String.valueOf(id));
        }
        staffRepository.setStatusById(false, id);
    }

    public Staff patch(Long id, Map<String, Object> updates) {
        Staff staff = staffRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );

        return staffRepository.save((Staff) entityService.fillFields(updates, staff));
    }
}
