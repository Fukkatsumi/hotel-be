package com.netcracker.hotelbe.service;

import com.netcracker.hotelbe.entity.Booking;
import com.netcracker.hotelbe.entity.Task;
import com.netcracker.hotelbe.entity.enums.TaskStatus;
import com.netcracker.hotelbe.repository.TaskRepository;
import com.netcracker.hotelbe.utils.CustomEntityLogMessage;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.persistence.EntityNotFoundException;
import java.sql.Timestamp;
import java.util.List;

import static org.hibernate.id.IdentifierGenerator.ENTITY_NAME;

@Service
public class TaskService {
    private static Logger logger = LogManager.getLogger(TaskService.class);
    private final static String ENTITY_NAME = Task.class.getSimpleName();
    @Autowired
    private TaskRepository taskRepository;

    public List<Task> findAll(){
        return taskRepository.findAll();
    }

    public Task findById(Long id){
        return taskRepository.findById(id).orElseThrow(
                ()->new EntityNotFoundException("No entity with id=" + id + " found")
        );
    }

    public Task save(Task task){
        return taskRepository.save(task);
    }

    public Long deleteById(Long id){
        if (!taskRepository.findById(id).isPresent()){
            throw new EntityNotFoundException("No entity with id=" + id + " found");
        }
        Task task = taskRepository.findById(id).get();
        final Long taskId = task.getId();
        task.setStatus(TaskStatus.Canceled);
        task = taskRepository.save(task);
        logger.trace(String.format(CustomEntityLogMessage.FOUND_ENTITY_WITH_ID, ENTITY_NAME, taskId));
        return task.getId();
    }


}
