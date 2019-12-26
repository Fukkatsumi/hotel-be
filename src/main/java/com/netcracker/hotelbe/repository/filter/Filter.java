package com.netcracker.hotelbe.repository.filter;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.ParseException;

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

    public int getSize(){
        return conditions.size();
    }

    private List<Predicate> buildPredicates(Root root, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        conditions.forEach(condition -> predicates.add(buildPredicate(condition, root, criteriaBuilder)));

        return predicates;
    }

    private Predicate buildPredicate(Condition condition, Root root, CriteriaBuilder criteriaBuilder) {
        switch (condition.getOperation()) {
            case equals:
                return buildEqualPredicate(condition, root, criteriaBuilder);
            case between:
                return buildBetweenPredicate(condition, root, criteriaBuilder);
            default:
                break;
        }
        return criteriaBuilder.equal(root.get(condition.getField()), condition.getValue());
    }

    private Predicate buildEqualPredicate(Condition condition, Root root, CriteriaBuilder criteriaBuilder){
        switch (condition.getType()) {
            case Long:
                return criteriaBuilder.equal(root.get(condition.getField()), Long.valueOf((String) condition.getValue()));
            case Date:
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date parsedDate = null;
                try {
                    parsedDate = dateFormat.parse((String) condition.getValue());
                    //TODO
                    //The database stores a date with a shift +2 hours
                    parsedDate.setHours(parsedDate.getHours()+2);
                } catch (ParseException ParseException) {
                    //TODO
                }
                Timestamp timestamp = Timestamp.valueOf(dateFormat.format(parsedDate));

                return criteriaBuilder.equal(root.get(condition.getField()), timestamp);
            default:
                return criteriaBuilder.equal(root.get(condition.getField()), condition.getValue());        }

    }

    private Predicate buildBetweenPredicate(Condition condition, Root root, CriteriaBuilder criteriaBuilder){
        switch (condition.getType()){
            case Long:
                Long firstLongValue = Long.valueOf(condition.getValue().toString().split(";")[0]);
                Long secondLongValue = Long.valueOf(condition.getValue().toString().split(";")[1]);

                return criteriaBuilder.between(root.<Long>get(condition.getField()), firstLongValue, secondLongValue);
            case Date:
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date parsedFirstDate = null;
                Date parsedSecondDate = null;
                try {
                    parsedFirstDate = dateFormat.parse(condition.getValue().toString().split(";")[0]);
                    parsedSecondDate = dateFormat.parse(condition.getValue().toString().split(";")[1]);

                    //TODO
                    //The database stores a date with a shift +2 hours
                    parsedFirstDate.setHours(parsedFirstDate.getHours()+2);
                    parsedSecondDate.setHours(parsedSecondDate.getHours()+2);
                } catch (ParseException ParseException) {
                    //TODO
                }
                Timestamp firstDate = Timestamp.valueOf(dateFormat.format(parsedFirstDate));
                Timestamp secondDate = Timestamp.valueOf(dateFormat.format(parsedSecondDate));

                return criteriaBuilder.between(root.<Date>get(condition.getField()), firstDate, secondDate);
            default:
                String firstValue = condition.getValue().toString().split(";")[0];
                String secondValue = condition.getValue().toString().split(";")[1];

                return criteriaBuilder.between(root.get(condition.getField()), firstValue, secondValue);
        }
    }
}
