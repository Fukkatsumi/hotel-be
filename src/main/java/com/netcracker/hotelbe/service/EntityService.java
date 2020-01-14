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
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class EntityService {
    private final static Logger LOG = Logger.getLogger("EntityService");

    public static final long SECOND = 1000; // in milli-seconds.
    public static final long MINUTE = 60 * SECOND;
    public static final long HOUR = 60 * MINUTE;
    public static final long DAY = 24 * HOUR;

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
                if (fieldClass.isEnum()) {
                    Method getValues = fieldClass.getDeclaredMethod("values");
                    Object obj = getValues.invoke(null);
                    Arrays.stream((Object[]) obj)
                            .filter(enumField -> enumField.toString().equalsIgnoreCase(v.toString()))
                            .findAny()
                            .ifPresent(enumValue -> {
                                try {
                                    method.invoke(object, enumValue);
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                    LOG.warning("Method " + methodName.toString() + " not found or illegal argument!");
                                }
                            });
                } else {
                    method.invoke(object, v);
                }
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
                    correctedDate.setTime(correctedDate.getTime() + dayShift * DAY);
                    break;
                case MINUS:
                    correctedDate.setTime(correctedDate.getTime() - dayShift * DAY);
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
                            correctedDate.setTime(correctedDate.getTime() + timeShift * DAY);
                            break;
                        case MINUS:
                            correctedDate.setTime(correctedDate.getTime() - timeShift * DAY);
                        default:
                            break;
                    }
                    break;
                case HOUR:
                    switch (operation) {
                        case PLUS:
                            correctedDate.setTime(correctedDate.getTime() + timeShift * HOUR);
                            break;
                        case MINUS:
                            correctedDate.setTime(correctedDate.getTime() - timeShift * HOUR);
                        default:
                            break;
                    }
                    break;
                case MINUTE:
                    switch (operation) {
                        case PLUS:
                            correctedDate.setTime(correctedDate.getTime() + timeShift * MINUTE);
                            break;
                        case MINUS:
                            correctedDate.setTime(correctedDate.getTime() - timeShift * MINUTE);
                        default:
                            break;
                    }
                    break;
                case SECOND:
                    switch (operation) {
                        case PLUS:
                            correctedDate.setTime(correctedDate.getTime() + timeShift * SECOND);
                            break;
                        case MINUS:
                            correctedDate.setTime(correctedDate.getTime() - timeShift * SECOND);
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
            Object copy = object.getClass().getDeclaredConstructor().newInstance();
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
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            LOG.warning("Error copying object!");
            return object;
        }
    }
}
