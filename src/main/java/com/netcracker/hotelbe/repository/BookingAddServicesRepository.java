package com.netcracker.hotelbe.repository;

import com.netcracker.hotelbe.entity.BookingAddServices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface BookingAddServicesRepository extends JpaRepository<BookingAddServices, Long>, JpaSpecificationExecutor<BookingAddServices> {

    @Transactional(readOnly = true)
    @Override
    Optional<BookingAddServices> findById(Long id);

    @Query(value = "SELECT bas.id, bas.service_name, bas.price FROM bookingaddservices bas",
            nativeQuery = true)
    List<BookingAddServices> findAllNative();
}
