package com.netcracker.hotelbe.repository;

import com.netcracker.hotelbe.entity.ApartmentPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ApartmentPriceRepository extends JpaRepository<ApartmentPrice, Long>, JpaSpecificationExecutor<ApartmentPrice> {

    @Transactional(readOnly = true)
    @Override
    Optional<ApartmentPrice> findById(Long id);

    @Query(value = "SELECT ap.id, ap.price, ap.start_period, ap.end_period, ap.apartment_class_id FROM apartmentprices ap",
            nativeQuery = true)
    List<ApartmentPrice> findAllNative();
}
