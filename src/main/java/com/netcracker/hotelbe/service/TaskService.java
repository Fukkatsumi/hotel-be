package com.netcracker.hotelbe.service;

import com.netcracker.hotelbe.entity.Task;
import com.netcracker.hotelbe.entity.enums.TaskStatus;
import com.netcracker.hotelbe.repository.TaskRepository;
import com.netcracker.hotelbe.repository.filter.Condition;
import com.netcracker.hotelbe.repository.filter.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public Task findById(Long id) {
        return taskRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.valueOf(id))
        );
    }

    public List<Task> getAllByParams(Map<String, String> allParams) {

        return taskRepository.findAll(fillFilter(allParams));
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

    private Filter fillFilter(final Map<String, String> allParams) {
        Filter filter = new Filter();
        if (allParams.get("executor") != null) {
            filter.addCondition(new Condition("executor", Long.valueOf(allParams.get("executor"))));
        }

        if (allParams.get("creator") != null) {
            filter.addCondition(new Condition("creator", Long.valueOf(allParams.get("creator"))));
        }

        if (allParams.get("end") != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date parsedDate = dateFormat.parse(allParams.get("end"));

                //The database stores a date with a shift +2 hours
                parsedDate.setHours(parsedDate.getHours() + 2);
                Timestamp timestamp = Timestamp.valueOf(dateFormat.format(parsedDate));

                filter.addCondition(new Condition("end", timestamp));
            } catch (ParseException ParseException){
                return filter;
            }

        }

        return filter;
    }
}
