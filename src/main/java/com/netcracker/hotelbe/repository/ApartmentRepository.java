package com.netcracker.hotelbe.repository;

import com.netcracker.hotelbe.entity.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ApartmentRepository extends JpaRepository<Apartment, Long>, JpaSpecificationExecutor<Apartment> {

    @Transactional(readOnly = true)
    @Override
    Optional<Apartment> findById(Long id);

    @Query(value = "SELECT a.id, a.room_number, a.photo, a.description, a.status, a.class_room FROM apartments a",
            nativeQuery = true)
    List<Apartment> findAllNative();
}
