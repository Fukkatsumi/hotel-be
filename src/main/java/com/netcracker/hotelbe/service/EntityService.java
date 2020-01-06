package com.netcracker.hotelbe.service;

import com.google.common.base.CaseFormat;
import com.netcracker.hotelbe.utils.enums.MathOperation;
import com.netcracker.hotelbe.utils.enums.UnitOfTime;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class EntityService {
    private final static Logger LOG = Logger.getLogger("ApartmentClassService");

    public Object fillFields(final Map<String, Object> fields, final Object targetObject) {
        Object object = getCopyFromObject(targetObject);
        Class clazz = object.getClass();

        fields.forEach((k, v) -> {
            StringBuilder methodName = new StringBuilder()
                    .append("set").append(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, k));
            try {
                Field field = clazz.getDeclaredField(k);
                Class fieldClass = field.getType();
                Method method = clazz.getDeclaredMethod(String.valueOf(methodName), fieldClass);
                method.invoke(object, v);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
                LOG.warning("Method " + methodName.toString() + " not found or illegal argument!");
            }

        });
        return object;
    }

    public Date correctingDate(Date date, MathOperation operation, int dayShift) {
        if (date == null) {
            return date;
        } else {
            Date correctedDate = (Date) date.clone();
            switch (operation) {
                case PLUS:
                    correctedDate.setDate(correctedDate.getDate() + dayShift);
                    break;
                case MINUS:
                    correctedDate.setDate(correctedDate.getDate() - dayShift);
                default:
                    break;
            }
        return correctedDate;
    }

}

    public Timestamp correctingTimestamp(Timestamp timestamp, MathOperation operation, UnitOfTime unitOfTime, int timeShift) {
        if (timestamp == null) {
            return timestamp;
        } else {
            Timestamp correctedDate = (Timestamp) timestamp.clone();
            switch (unitOfTime) {
                case DAY:
                    switch (operation) {
                        case PLUS:
                            correctedDate.setDate(correctedDate.getDate() + timeShift);
                            break;
                        case MINUS:
                            correctedDate.setDate(correctedDate.getDate() - timeShift);
                        default:
                            break;
                    }
                    break;
                case HOUR:
                    switch (operation) {
                        case PLUS:
                            correctedDate.setHours(correctedDate.getHours() + timeShift);
                            break;
                        case MINUS:
                            correctedDate.setHours(correctedDate.getHours() - timeShift);
                        default:
                            break;
                    }
                    break;
                case MINUTE:
                    switch (operation) {
                        case PLUS:
                            correctedDate.setMinutes(correctedDate.getMinutes() + timeShift);
                            break;
                        case MINUS:
                            correctedDate.setMinutes(correctedDate.getMinutes() - timeShift);
                        default:
                            break;
                    }
                    break;
                case SECOND:
                    switch (operation) {
                        case PLUS:
                            correctedDate.setSeconds(correctedDate.getSeconds() + timeShift);
                            break;
                        case MINUS:
                            correctedDate.setSeconds(correctedDate.getSeconds() - timeShift);
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
            return correctedDate;
        }
    }

    private Object getCopyFromObject(final Object object) {
        try {
            Object copy = object.getClass().newInstance();
            Class clazz = copy.getClass();
            Field[] fields = object.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                StringBuilder setMethodName = new StringBuilder()
                        .append("set").append(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, fields[i].getName()));
                StringBuilder getMethodName = new StringBuilder()
                        .append("get").append(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, fields[i].getName()));
                Class fieldClass = fields[i].getType();
                try {
                    Method setMethod = clazz.getDeclaredMethod(String.valueOf(setMethodName), fieldClass);
                    Method getMethod = clazz.getDeclaredMethod(String.valueOf(getMethodName));
                    setMethod.invoke(copy, getMethod.invoke(object));
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    LOG.warning("Method " + setMethodName.toString() + " not found or illegal argument!");
                }
            }
            return copy;
        } catch (InstantiationException | IllegalAccessException e) {
            LOG.warning("Error copying object!");
            return object;
        }
    }
}
