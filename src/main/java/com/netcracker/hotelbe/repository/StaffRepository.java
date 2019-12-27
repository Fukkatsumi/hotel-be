package com.netcracker.hotelbe.repository;

import com.netcracker.hotelbe.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long>, JpaSpecificationExecutor<Staff> {

    @Transactional
    @Modifying
    @Query("UPDATE Staff s SET s.active = :active WHERE s.id = :id")
    public void setStatusById(@Param("active") Boolean active, @Param("id") Long id);
}
