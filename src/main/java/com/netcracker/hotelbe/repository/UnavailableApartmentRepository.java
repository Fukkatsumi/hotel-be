package com.netcracker.hotelbe.repository;

import com.netcracker.hotelbe.entity.UnavailableApartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


public interface UnavailableApartmentRepository extends JpaRepository<UnavailableApartment, Long>, JpaSpecificationExecutor<UnavailableApartment> {

    @Transactional(readOnly = true)
    @Override
    Optional<UnavailableApartment> findById(Long id);
}
