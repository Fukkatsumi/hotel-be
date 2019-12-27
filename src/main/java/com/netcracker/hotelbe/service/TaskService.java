package com.netcracker.hotelbe.service;

import com.netcracker.hotelbe.entity.Task;
import com.netcracker.hotelbe.entity.enums.TaskStatus;
import com.netcracker.hotelbe.repository.TaskRepository;
import com.netcracker.hotelbe.service.filter.FilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;


@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private FilterService filterService;

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public Task findById(Long id) {
        return taskRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );
    }

    public List<Task> getAllByParams(Map<String, String> allParams) {
        if(allParams.size()!=0) {
            return taskRepository.findAll(filterService.fillFilter(allParams, Task.class));
        } else {
            return taskRepository.findAll();
        }
    }

    public Task save(Task task) {
        return taskRepository.save(task);
    }

    public Task update(Task task, Long id) {
        taskRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );

        task.setId(id);

        return taskRepository.save(task);
    }

    public void deleteById(Long id) {
        if (!taskRepository.findById(id).isPresent()) {
            throw new EntityNotFoundException(String.valueOf(id));
        }
        Task task = taskRepository.findById(id).get();
        task.setStatus(TaskStatus.Canceled);
        taskRepository.save(task);
    }

}
