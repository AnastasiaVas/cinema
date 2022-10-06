package com.test.cinema.specification;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchCriteria {
    private String key;
    private String operation;
    private Object value;
    private Boolean isOr = false;

    public SearchCriteria(String key, String operation, Object value, Boolean isOr) {
        this.key = key;
        this.operation = operation;
        this.value = value;
        this.isOr = isOr;
    }

    public SearchCriteria(String key, String operation, Object value) {
        this.key = key;
        this.operation = operation;
        this.value = value;
    }
}
