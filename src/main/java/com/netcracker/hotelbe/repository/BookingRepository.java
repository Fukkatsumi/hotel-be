package com.netcracker.hotelbe.repository;

import com.netcracker.hotelbe.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Transactional(readOnly = true)
    @Override
    Optional<Booking> findById(Long id);
}
