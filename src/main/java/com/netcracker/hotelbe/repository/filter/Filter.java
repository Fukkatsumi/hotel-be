package com.netcracker.hotelbe.repository.filter;

import org.springframework.data.jpa.domain.Specification;

public interface Filter extends Specification {

    void addCondition(Condition condition);

    int getSize();
}
