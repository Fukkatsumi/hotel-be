package com.netcracker.hotelbe.repository.filter;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class Filter implements Specification {
    private List<Condition> conditions;

    public Filter() {
        conditions = new ArrayList<>();
    }

    @Override
    public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = buildPredicates(root, criteriaBuilder);

        return predicates.size() > 1
                ? criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]))
                : predicates.get(0);
    }

    public void addCondition(Condition condition) {
        conditions.add(condition);
    }

    private List<Predicate> buildPredicates(Root root, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        conditions.forEach(condition -> predicates.add(buildPredicate(condition, root, criteriaBuilder)));

        return predicates;
    }

    private Predicate buildPredicate(Condition condition, Root root, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.equal(root.get(condition.getField()), condition.getValue());
    }
}
