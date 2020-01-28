package com.netcracker.hotelbe.repository;

import com.netcracker.hotelbe.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {

    @Transactional(readOnly = true)
    @Override
    Optional<Booking> findById(Long id);

    @Query(value = "SELECT b.id, b.start_date, b.end_date, b.total_price, b.comments, b.created_date, b.review, b.booking_status FROM Bookings b",
            nativeQuery = true)
    List<Booking> findAllNative();

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM bookings b WHERE b.id =:id and b.booking_status = 'Created'", nativeQuery = true)
    void deleteCreatedBookingById(@Param("id") Long id);

}
