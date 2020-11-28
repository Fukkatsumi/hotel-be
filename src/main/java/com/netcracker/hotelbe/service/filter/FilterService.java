package com.netcracker.hotelbe.service.filter;

import com.netcracker.hotelbe.repository.filter.Filter;
import com.netcracker.hotelbe.repository.filter.impl.FilterImpl;
import com.netcracker.hotelbe.utils.enums.RegEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Map;

@Service
public class FilterService {

    @Autowired
    private ConditionService conditionService;

    public Filter fillFilter(final Map<String, String> allParams, Class clazz) {
        Filter filter = new FilterImpl();
        allParams.forEach((k, v) -> {
            try {
                Field field = clazz.getDeclaredField(k);
                if (field != null) {
                    Class fieldClass = field.getType();
                    if (v.matches(RegEx.LONG.getFullName())) {
                        filter.addCondition(conditionService.getConditionFromStringNumber(k, v));
                    } else if ((v.matches(RegEx.DATE.getFullName())
                            && (fieldClass.equals(Date.class) || fieldClass.equals(Timestamp.class)))
                            || v.equalsIgnoreCase("null")) {
                        filter.addCondition(conditionService.getConditionFromDate(k, v));

                    } else if (fieldClass.isEnum()) {
                        Method method = fieldClass.getDeclaredMethod("values");
                        Object obj = method.invoke(null);
                        Arrays.stream((Object[]) obj)
                                .filter(enumField -> enumField.toString().equalsIgnoreCase(v))
                                .findAny()
                                .ifPresent(enumValue -> filter.addCondition(conditionService.getConditionFromEnum(k, enumValue)));
                    } else if (fieldClass.equals(Boolean.TYPE)) {
                        if (v.equalsIgnoreCase("true") || v.equalsIgnoreCase("false")
                                || v.equals("1") || v.equals("0")) {
                            filter.addCondition(conditionService.getConditionFromStringBoolean(k, v));
                        }
                    } else {
                        filter.addCondition(conditionService.getConditionFromString(k, v));
                    }

                }

            } catch (NoSuchFieldException | NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
                //TODO
            }
        });

        if (filter.getSize() == 0) {
            filter.addCondition(conditionService.getDefaultCondition());
        }
        return filter;
    }


}
