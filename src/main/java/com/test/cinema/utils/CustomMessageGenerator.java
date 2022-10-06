package com.test.cinema.utils;

import com.test.cinema.utils.enums.Entities;
import com.test.cinema.utils.enums.EntityFields;

public final class CustomMessageGenerator {

    private CustomMessageGenerator() {
    }

    public static String entityDoesntExist(Entities entity, EntityFields entityField, String field){
        return String.format("%s with %s %s doesn't exist.", entity.getEntityName(), entityField.getFieldName(), field);
    }

    public static String entityDeleted(Entities entity, EntityFields entityField, String field) {
        return String.format("%s with %s %s was successfully deleted.", entity.getEntityName(), entityField.getFieldName(), field);
    }
}
