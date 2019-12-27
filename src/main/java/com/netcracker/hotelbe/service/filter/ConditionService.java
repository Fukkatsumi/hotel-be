package com.netcracker.hotelbe.service.filter;

import com.netcracker.hotelbe.repository.filter.Condition;
import com.netcracker.hotelbe.repository.filter.Operation;
import com.netcracker.hotelbe.repository.filter.Type;
import com.netcracker.hotelbe.utils.enums.RegEx;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.sql.Timestamp;

@Service
public class ConditionService {

    public Condition getConditionFromDate(final String name, final String value){
        Condition condition = new Condition();
        condition.setField(name);

        String[] fullDate = value.split(" ");
        StringBuilder parsedDate = new StringBuilder();
        StringBuilder firstDate = new StringBuilder();
        StringBuilder secondDate = new StringBuilder();

        switch (fullDate.length) {
            //format "yyyy-MM-dd"
            case 1:
                firstDate.append(value).append(" 00:00:00");
                secondDate.append(value).append(" 23:59:59");
                break;
            case 2:
                switch (fullDate[1].split(":").length) {
                    //format "yyyy-MM-dd HH"
                    case 1:
                        firstDate.append(value).append(":00:00");
                        secondDate.append(value).append(":59:59");
                        break;

                    //format "yyyy-MM-dd HH:mm"
                    case 2:
                        firstDate.append(value).append(":00");
                        secondDate.append(value).append(":59");
                        break;

                    //format "yyyy-MM-dd HH:mm:ss"
                    case 3:
                        parsedDate.append(value);
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }

        if (value.contentEquals(parsedDate)) {
            condition.setValue(value);
            condition.setOperation(Operation.equals);
        }

        if (firstDate.toString().matches(RegEx.DATE.getFullName()) && secondDate.toString().matches(RegEx.DATE.getFullName())) {
            parsedDate.append(firstDate).append(";").append(secondDate);
            condition.setValue(parsedDate);
            condition.setOperation(Operation.between);
        }
        return condition;
    }

    public Condition getConditionFromString(String name, String value){
        return new Condition(name, value, Type.Object, Operation.equals);
    }

    public Condition getDefaultCondition(){
        return new Condition("id", -1, Type.Long, Operation.equals);
    }

    public Condition getConditionFromEnum(String fieldName, Object value){
       return new Condition(fieldName, value, Type.Object, Operation.equals);
    }
}
