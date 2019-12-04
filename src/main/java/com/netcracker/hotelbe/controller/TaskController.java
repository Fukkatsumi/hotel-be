package com.netcracker.hotelbe.controller;

import com.netcracker.hotelbe.entity.Task;
import com.netcracker.hotelbe.service.TaskService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("task")
public class TaskController {
    private final static Logger logger = Logger.getLogger(TaskController.class);
    private final static String TASK_BY_ID_NOT_FOUND = "Task by id: %d not found!";

    @Autowired
    private TaskService taskService;

    @Autowired
    @Qualifier("taskValidator")
    private Validator taskValidator;

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(){
        return new ResponseEntity<>(taskService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id){
        return new ResponseEntity<>(taskService.findById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Long> addTask(@RequestBody @Valid Task task, BindingResult bindingResult) throws MethodArgumentNotValidException {
        taskValidator.validate(task, bindingResult);
        if(bindingResult.hasErrors()){
            throw new MethodArgumentNotValidException(null, bindingResult);
        }
        return new ResponseEntity<>(taskService.save(task).getId(), HttpStatus.OK );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTaskById(@RequestBody @Valid Task task, @PathVariable("id") Long id, BindingResult bindingResult) throws MethodArgumentNotValidException{
        task.setId(id);
        taskValidator.validate(task, bindingResult);
        if(bindingResult.hasErrors()){
            throw new MethodArgumentNotValidException(null, bindingResult);
        }
        return new ResponseEntity<>(taskService.save(task), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    private ResponseEntity deleteTaskById(@PathVariable Long id){
        logger.info("Request for delete task by id: " +id);

        Long delete = taskService.deleteById(id);
        if (delete != 0) {
            return new ResponseEntity(HttpStatus.OK);
        } else {
            logger.warn(String.format(TASK_BY_ID_NOT_FOUND, id));
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }


}
