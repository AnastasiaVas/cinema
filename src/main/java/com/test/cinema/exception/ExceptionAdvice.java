package com.test.cinema.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;

@ControllerAdvice
public class ExceptionAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> illegalArgumentException(IllegalArgumentException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value
            = {EntityNotFoundException.class})
    protected ResponseEntity<Object> entityNotFoundExceptionHandler(
            RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @NonNull
    @Override
    protected ResponseEntity<Object>
    handleMethodArgumentNotValid(MethodArgumentNotValidException e,
                                 @NonNull HttpHeaders headers, @NonNull HttpStatus status,
                                 @NonNull WebRequest request) {
        BindingResult bindingResult = e.getBindingResult();
        IncorrectlyFilledFieldsExceptionMessage incorrectlyFilledFieldsExceptionMessage = new IncorrectlyFilledFieldsExceptionMessage();
        incorrectlyFilledFieldsExceptionMessage.setExceptionMessage("Please, input correct values.");
        bindingResult.getFieldErrors().forEach(error -> incorrectlyFilledFieldsExceptionMessage.
                addIncorrectFilledFieldsMessages(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(incorrectlyFilledFieldsExceptionMessage, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}