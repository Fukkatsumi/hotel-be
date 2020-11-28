package com.netcracker.hotelbe.repository;

import com.netcracker.hotelbe.entity.ApartmentClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ApartmentClassRepository extends JpaRepository<ApartmentClass, Long>, JpaSpecificationExecutor<ApartmentClass> {

    @Transactional(readOnly = true)
    @Override
    Optional<ApartmentClass> findById(Long id);

    @Query(value = "SELECT ac.id, ac.name_class, ac.number_of_rooms, ac.number_of_couchette FROM APARTMENTCLASS ac",
            nativeQuery = true)
    List<ApartmentClass> findAllNative();

}
