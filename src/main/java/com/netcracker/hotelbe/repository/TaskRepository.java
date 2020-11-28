package com.netcracker.hotelbe.repository;

import com.netcracker.hotelbe.entity.Staff;
import com.netcracker.hotelbe.entity.Task;
import com.netcracker.hotelbe.entity.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    @Transactional
    @Modifying
    @Query("UPDATE Task t SET t.status = :new_status WHERE t.id = :task_id")
    void updateStatusById(@Param("task_id") Long id, @Param("new_status")TaskStatus taskStatus);

    @Transactional
    @Modifying
    @Query("UPDATE Task t SET t.executor = :executor WHERE t.id = :task_id")
    void setExecutorByTaskId(@Param("task_id") Long id, @Param("executor") Staff executor);

    @Query(value = "SELECT t.id, t.start_date, t.end_date, t.accept_date, t.complete_date, t.description, t.task_status, t.apartment_id, t.executor_id, t.creator_id FROM tasks t",
            nativeQuery = true)
    List<Task> findAllNative();

    List<Task> getAllByExecutor(Staff staff);

    List<Task> getAllByCreator(Staff staff);

    List<Task> getAllByStartBetween(Timestamp start, Timestamp end);

    List<Task> getAllByExecutorAndStartBetween(Staff staff, Timestamp start, Timestamp end);
}
