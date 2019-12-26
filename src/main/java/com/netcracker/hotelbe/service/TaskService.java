package com.netcracker.hotelbe.service;

import com.netcracker.hotelbe.entity.Task;
import com.netcracker.hotelbe.entity.enums.TaskStatus;
import com.netcracker.hotelbe.repository.TaskRepository;
import com.netcracker.hotelbe.repository.filter.Condition;
import com.netcracker.hotelbe.repository.filter.Filter;
import com.netcracker.hotelbe.repository.filter.Operation;
import com.netcracker.hotelbe.repository.filter.Type;
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
            filter.addCondition(new Condition("executor", allParams.get("executor"),
                    Type.Long, Operation.equals));
        }

        if (allParams.get("creator") != null) {
            filter.addCondition(new Condition("creator", allParams.get("creator"),
                    Type.Long, Operation.equals));
        }

        if (allParams.get("end") != null) {
            String dateRegex = "(^\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2})|(^\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2})|(^\\d{4}-\\d{2}-\\d{2}\\s\\d{2})|(^\\d{4}-\\d{2}-\\d{2})";
            if (allParams.get("end").matches(dateRegex)) {

                String[] fullDate = allParams.get("end").split(" ");

                StringBuilder parsedDate = new StringBuilder();
                StringBuilder firstDate = new StringBuilder();
                StringBuilder secondDate = new StringBuilder();

                switch (fullDate.length) {
                    case 1:
                        //format "yyyy-MM-dd"
                        firstDate.append(allParams.get("end")).append(" 00:00:00");
                        secondDate.append(allParams.get("end")).append(" 23:59:59");
                        break;
                    case 2:
                        switch (fullDate[1].split(":").length) {
                            case 1:
                                //format "yyyy-MM-dd HH"
                                firstDate.append(allParams.get("end")).append(":00:00");
                                secondDate.append(allParams.get("end")).append(":59:59");
                                break;
                            case 2:
                                //format "yyyy-MM-dd HH:mm"
                                firstDate.append(allParams.get("end")).append(":00");
                                secondDate.append(allParams.get("end")).append(":59");
                                break;
                            case 3:
                                //format "yyyy-MM-dd HH:mm:ss"
                                parsedDate.append(allParams.get("end"));
                                break;
                            default:
                                break;
                        }
                        break;
                    default:
                        break;
                }

                if (allParams.get("end").contentEquals(parsedDate)) {
                    filter.addCondition(new Condition("end", allParams.get("end"), Type.Date, Operation.equals));
                }

                if (firstDate.toString().matches(dateRegex) && secondDate.toString().matches(dateRegex)) {
                    parsedDate.append(firstDate).append(";").append(secondDate);
                    filter.addCondition(new Condition("end", parsedDate, Type.Date, Operation.between));
                }
            }
        }

        if (filter.getSize() == 0) {
            filter.addCondition(new Condition("end", new Timestamp(0), Type.Object, Operation.equals));
        }

        return filter;
    }
}
