package com.test.cinema.specification;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@RequiredArgsConstructor
public class EntitySpecification<T> implements Specification<T> {
    private final SearchCriteria searchCriteria;

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return switch (searchCriteria.getOperation()) {
            case "=" -> criteriaBuilder.equal(root.get(searchCriteria.getKey()), searchCriteria.getValue());
            case "!=" -> criteriaBuilder.notEqual(root.get(searchCriteria.getKey()), searchCriteria.getValue());
            case "is" -> criteriaBuilder.isNull(root.get(searchCriteria.getKey()));
            case ">" -> criteriaBuilder.greaterThan(root.get(
                    searchCriteria.getKey()), searchCriteria.getValue().toString());
            case "<" -> criteriaBuilder.lessThan(root.get(
                    searchCriteria.getKey()), searchCriteria.getValue().toString());
            default ->  criteriaBuilder.equal(root.get("id"), -1);
        };
    }
}