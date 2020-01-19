package com.netcracker.hotelbe.repository.filter.impl;

import com.netcracker.hotelbe.repository.filter.Condition;
import com.netcracker.hotelbe.repository.filter.enums.Operation;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
public class ConditionImpl implements Condition {
    private String field;
    private Object value;
    private Operation operation;

    @Override
    public void setField(String field) {
        this.field = field;
    }

    @Override
    public String getField() {
        return field;
    }

    @Override
    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    @Override
    public Operation getOperation() {
        return operation;
    }
}
