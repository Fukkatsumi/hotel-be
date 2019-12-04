package com.netcracker.hotelbe.repository;

import com.netcracker.hotelbe.entity.ApartmentPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface ApartmentPriceRepository extends JpaRepository<ApartmentPrice, Long> {

    @Transactional(readOnly = true)
    @Override
    Optional<ApartmentPrice> findById(Long id);
}
