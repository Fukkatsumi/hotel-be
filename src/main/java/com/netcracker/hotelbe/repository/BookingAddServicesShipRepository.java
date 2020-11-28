package com.netcracker.hotelbe.repository;

import com.netcracker.hotelbe.entity.BookingAddServicesShip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface BookingAddServicesShipRepository extends JpaRepository<BookingAddServicesShip, Long>, JpaSpecificationExecutor<BookingAddServicesShip> {

    @Transactional(readOnly = true)
    @Override
    Optional<BookingAddServicesShip> findById(Long id);

    @Query(value = "SELECT bass.id, bass.booking_id, bass.add_service_id_booking, bass.count_services FROM bookingaddservicesship bass",
            nativeQuery = true)
    List<BookingAddServicesShip> findAllNative();

    @Transactional
    void deleteAllByBooking_Id(Long aLong);
}
