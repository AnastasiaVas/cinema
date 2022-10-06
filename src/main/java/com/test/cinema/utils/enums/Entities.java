package com.test.cinema.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Entities {

    MOVIE("Movie"),
    ORDER("Order");

    private final String entityName;
}
