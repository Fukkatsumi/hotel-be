package com.netcracker.hotelbe.service.filter;

import com.netcracker.hotelbe.repository.filter.Condition;
import com.netcracker.hotelbe.repository.filter.impl.ConditionImpl;
import com.netcracker.hotelbe.repository.filter.enums.Operation;
import com.netcracker.hotelbe.utils.enums.RegEx;
import org.springframework.stereotype.Service;

@Service
public class ConditionService {

    public Condition getConditionFromDate(final String name, final String value) {
        Condition condition = new ConditionImpl();
        condition.setField(name);

        if (value.equalsIgnoreCase("null")) {
            condition.setOperation(Operation.IS_NULL);
            return condition;
        }

        String[] fullDate = value.split(" ");
        StringBuilder parsedDate = new StringBuilder();
        StringBuilder firstDate = new StringBuilder();
        StringBuilder secondDate = new StringBuilder();

        switch (fullDate.length) {
            //format "yyyy-MM-dd"
            case 1:
                firstDate.append(value).append(" ").append("00").append(":").append("00").append(":").append("00");
                secondDate.append(value).append(" ").append("23").append(":").append("59").append(":").append("59");
                break;
            case 2:
                switch (fullDate[1].split(":").length) {
                    //format "yyyy-MM-dd HH"
                    case 1:
                        firstDate.append(value).append(":").append("00").append(":").append("00");
                        secondDate.append(value).append(":").append("59").append(":").append("59");
                        break;

                    //format "yyyy-MM-dd HH:mm"
                    case 2:
                        firstDate.append(value).append(":").append("00");
                        secondDate.append(value).append(":").append("59");
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
            condition.setOperation(Operation.EQUALS);
        }

        if (firstDate.toString().matches(RegEx.DATE.getFullName()) && secondDate.toString().matches(RegEx.DATE.getFullName())) {
            parsedDate.append(firstDate).append(";").append(secondDate);
            condition.setValue(parsedDate);
            condition.setOperation(Operation.BETWEEN);
        }
        return condition;
    }

    public Condition getConditionFromString(String name, String value) {
        return new ConditionImpl(name, value, Operation.LIKE);
    }

    public Condition getDefaultCondition() {
        return new ConditionImpl("id", -1, Operation.EQUALS);
    }

    public Condition getConditionFromEnum(String fieldName, Object value) {
        return new ConditionImpl(fieldName, value, Operation.EQUALS);
    }

    public Condition getConditionFromStringBoolean(String name, String value) {
        boolean val = value.equalsIgnoreCase("true") || value.equals("1");

        return new ConditionImpl(name, val, Operation.EQUALS);
    }

    public Condition getConditionFromStringNumber(String name, String value){
        return new ConditionImpl(name, value, Operation.EQUALS);
    }
}
