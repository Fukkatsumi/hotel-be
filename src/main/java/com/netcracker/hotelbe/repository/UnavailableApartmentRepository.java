package com.netcracker.hotelbe.repository;

import com.netcracker.hotelbe.entity.UnavailableApartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


public interface UnavailableApartmentRepository extends JpaRepository<UnavailableApartment, Long>, JpaSpecificationExecutor<UnavailableApartment> {

    @Transactional(readOnly = true)
    @Override
    Optional<UnavailableApartment> findById(Long id);

    @Query(value = "SELECT ua.id, ua.id_apartment, ua.start_date, ua.end_date, ua.cause_description FROM unavailableapartments ua",
            nativeQuery = true)
    List<UnavailableApartment> findAllNative();
}
