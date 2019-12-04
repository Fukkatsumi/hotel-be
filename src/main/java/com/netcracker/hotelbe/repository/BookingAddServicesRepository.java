package com.netcracker.hotelbe.repository;

import com.netcracker.hotelbe.entity.Booking;
import com.netcracker.hotelbe.entity.BookingAddServices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface BookingAddServicesRepository extends JpaRepository<BookingAddServices, Long> {

    @Transactional(readOnly = true)
    @Override
    Optional<BookingAddServices> findById(Long id);
}
