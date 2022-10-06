package com.test.cinema.exception;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class IncorrectlyFilledFieldsExceptionMessage {

    private String exceptionMessage;
    private Map<String, String> incorrectFilledFieldsMessages;

    public void addIncorrectFilledFieldsMessages(String key, String value){
        if (incorrectFilledFieldsMessages == null){
            incorrectFilledFieldsMessages = new HashMap<>();
        }
        incorrectFilledFieldsMessages.put(key, value);
    }
}
