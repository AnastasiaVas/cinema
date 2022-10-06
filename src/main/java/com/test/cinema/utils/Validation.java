package com.test.cinema.utils;

import com.test.cinema.utils.enums.Entities;
import com.test.cinema.utils.enums.EntityFields;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.function.Consumer;

public class Validation{

    private static final String ERROR_TEXT_ID = "Id should be a positive digit.";

    public static <T> void throwIfNotPresent(Optional<T> optional, Entities entity, EntityFields entityField, String value) {
        optional.orElseThrow(() -> new EntityNotFoundException(CustomMessageGenerator
                .entityDoesntExist(entity, entityField, String.valueOf(value))));
    }

    public static Consumer<Integer> idValidation = (value) -> {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(ERROR_TEXT_ID);
        }
    };
}

