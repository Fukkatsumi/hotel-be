package com.netcracker.hotelbe.service;

import com.netcracker.hotelbe.entity.Task;
import com.netcracker.hotelbe.entity.enums.TaskStatus;
import com.netcracker.hotelbe.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.persistence.Table;
import java.util.List;


@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public List<Task> findAll(){
        return taskRepository.findAll();
    }

    public Task findById(Long id){
        return taskRepository.findById(id).orElseThrow(
                ()->new EntityNotFoundException(String.valueOf(id))
        );
    }

    public Task save(Task task){
        return taskRepository.save(task);
    }

    public Task update(Task task, Long id){
        taskRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );

        task.setId(id);

        return taskRepository.save(task);
    }

    public void deleteById(Long id){
        if (!taskRepository.findById(id).isPresent()){
            throw new EntityNotFoundException(String.valueOf(id));
        }
        Task task = taskRepository.findById(id).get();
        task.setStatus(TaskStatus.Canceled);
        taskRepository.save(task);
    }
}
