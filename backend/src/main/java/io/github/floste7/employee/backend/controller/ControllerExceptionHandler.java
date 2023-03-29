package io.github.floste7.employee.backend.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.stream.Collectors;


@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<DefaultError> unknownException(Exception ex, WebRequest wr) {
        log.warn("Unable to handle {}", wr.getDescription(false), ex);

        return new ResponseEntity<>(new DefaultError(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<DefaultError> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest wr) {
        log.warn("Unable to handle {}: {}", wr.getDescription(false), ex.getMessage());

        return new ResponseEntity<>(new DefaultError(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<DefaultError> handleDataIntegrityException(DataIntegrityViolationException ex, WebRequest wr) {
        log.warn("Unable to handle {}: {}", wr.getDescription(false), ex.getMessage());

        return new ResponseEntity<>(new DefaultError("E-Mail addresses must be unique!"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DefaultError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest wr) {
        log.warn("Unable to handle {}: {}", wr.getDescription(false), ex.getMessage());

        String errorMessages = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(","));

        return new ResponseEntity<>(new DefaultError(errorMessages), HttpStatus.BAD_REQUEST);
    }

    @Getter
    private static class DefaultError {
        private final Date date;
        private final String message;

        private DefaultError(String message) {
            this.date = new Date();
            this.message = message;
        }
    }
}
