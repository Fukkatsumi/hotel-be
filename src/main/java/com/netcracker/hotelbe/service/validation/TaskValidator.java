package com.netcracker.hotelbe.service.validation;

import com.netcracker.hotelbe.entity.Staff;
import com.netcracker.hotelbe.entity.Task;
import com.netcracker.hotelbe.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.validation.ConstraintViolation;
import java.sql.Timestamp;
import java.util.Set;

@Service
public class TaskValidator implements Validator {

    @Autowired
    private javax.validation.Validator JValidator;

    @Autowired
    private StaffService staffService;

    @Override
    public boolean supports(Class<?> aClass) {
        return Task.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {

        //Get declarative validation results
        Set<ConstraintViolation<Object>> validates = JValidator.validate(o);

        validates.forEach(
                cv -> errors.rejectValue(
                        cv.getPropertyPath().toString(),
                        "",
                        cv.getMessage())
        );

        //Spring validation
        Task task = (Task) o;
        //120000ms - is 2 minutes, it was added to compensate server delay
        Timestamp currentTime = new Timestamp(System.currentTimeMillis() - 120000);

        if (task.getStart().compareTo(currentTime) < 0) {
            errors.rejectValue("start", "", "Start date cant be before current date ");
        }
        if (task.getEnd().compareTo(currentTime) < 0) {
            errors.rejectValue("end", "", "End date cant be before current date ");
        }
        if (task.getEnd().compareTo(task.getStart()) < 0) {
            errors.rejectValue("end", "", "End date cant be before Start date");
        }


        if (task.getComplete() != null)
            if (task.getComplete().compareTo(task.getAccept()) < 0) {
                errors.rejectValue("complete", "", "Complete date cant be before Start date");
            }

        if (task.getExecutor() != null) {
            Staff executor = staffService.findById(task.getExecutor().getId());
            if (!executor.isActive()) {
                errors.rejectValue("executor", "", "Executor should be active");
            }
        }

        if (task.getCreator() != null) {
            Staff creator = staffService.findById(task.getCreator().getId());
            if (!creator.isActive()) {
                errors.rejectValue("creator", "", "Creator should be active");
            }
        }
    }
}
