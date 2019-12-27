package com.netcracker.hotelbe.repository.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Condition {
    private String field;
    private Object value;
    private Type type;
    private Operation operation;

}
