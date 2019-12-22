package com.netcracker.hotelbe.service;

import com.netcracker.hotelbe.entity.Staff;
import com.netcracker.hotelbe.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class StaffService {

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private UserService userService;

    public Staff findById(long id) {
        return staffRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );
    }

    public List<Staff> findAll() {
        return staffRepository.findAll();
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
}
