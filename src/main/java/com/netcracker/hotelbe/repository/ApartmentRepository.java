package com.netcracker.hotelbe.repository;

import com.netcracker.hotelbe.entity.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface ApartmentRepository extends JpaRepository<Apartment, Long>, JpaSpecificationExecutor<Apartment> {

    @Transactional(readOnly = true)
    @Override
    Optional<Apartment> findById(Long id);
}
