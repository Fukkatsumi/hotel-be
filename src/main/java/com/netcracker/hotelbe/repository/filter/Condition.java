package com.netcracker.hotelbe.repository.filter;

public class Condition {
    private String field;
    private Object value;
    private Type type;
    private Operation operation;

    public Condition(String field, Object value, Type type, Operation operation) {
        this.field = field;
        this.value = value;
        this.type = type;
        this.operation = operation;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }
}
