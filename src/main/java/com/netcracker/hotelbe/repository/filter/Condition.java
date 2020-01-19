package com.netcracker.hotelbe.repository.filter;

import com.netcracker.hotelbe.repository.filter.enums.Operation;

public interface Condition {
    void setField(String field);

    String getField();

    void setValue(Object value);

    Object getValue();

    void setOperation(Operation operation);

    Operation getOperation();
}
