package com.netcracker.hotelbe.repository;

import com.netcracker.hotelbe.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {

    @Transactional(readOnly = true)
    @Override
    Optional<Booking> findById(Long id);
}
