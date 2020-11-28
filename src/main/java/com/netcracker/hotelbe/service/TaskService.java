package com.netcracker.hotelbe.service;

import com.netcracker.hotelbe.entity.Task;
import com.netcracker.hotelbe.entity.enums.TaskStatus;
import com.netcracker.hotelbe.repository.TaskRepository;
import com.netcracker.hotelbe.service.filter.FilterService;
import com.netcracker.hotelbe.utils.enums.MathOperation;
import com.netcracker.hotelbe.utils.enums.UnitOfTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;


@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private FilterService filterService;

    @Autowired
    private EntityService entityService;

    public List<Task> findAll() {
        List<Task> tasks = taskRepository.findAll();
        tasks.forEach(this::correctingDate);

        return  tasks;
    }

    public Task findById(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );

        return correctingDate(task);
    }

    public List<Task> getAllByParams(Map<String, String> allParams) {
        List<Task> tasks;
        if (allParams.size() != 0) {
            tasks = taskRepository.findAll(filterService.fillFilter(allParams, Task.class));
        } else {
            tasks = taskRepository.findAllNative();
        }
        tasks.forEach(this::correctingDate);

        return tasks;
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

    public Task patch(Long id, Map<String, Object> updates) {
        Task task = taskRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );

        return taskRepository.save((Task) entityService.fillFields(updates, task));
    }

    private Task correctingDate(Task task) {

        Timestamp startDate = entityService.correctingTimestamp(task.getStart(), MathOperation.PLUS, UnitOfTime.HOUR, +2);
        task.setStart(startDate);

        Timestamp endDate = entityService.correctingTimestamp(task.getEnd(), MathOperation.PLUS, UnitOfTime.HOUR, +2);
        task.setEnd(endDate);

        Timestamp acceptDate = entityService.correctingTimestamp(task.getAccept(), MathOperation.PLUS, UnitOfTime.HOUR, +2);
        task.setAccept(acceptDate);

        Timestamp completeDate = entityService.correctingTimestamp(task.getComplete(), MathOperation.PLUS, UnitOfTime.HOUR, +2);
        task.setComplete(completeDate);

        return task;
    }
}
