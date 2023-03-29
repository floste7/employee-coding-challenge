package io.github.floste7.employee.backendread.controller;

import io.github.floste7.employee.backendread.exception.EntityNotFoundException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;


@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

    /**
     * Handles unknown Exception and server errors
     *
     * @param ex thrown exception
     * @param wr web request
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<DefaultError> unknownException(Exception ex, WebRequest wr) {
        log.warn("Unable to handle {}", wr.getDescription(false), ex);

        return new ResponseEntity<>(new DefaultError(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<DefaultError> entityNotFoundException(EntityNotFoundException ex, WebRequest wr) {
        log.warn("Unable to handle {}", wr.getDescription(false), ex);

        return new ResponseEntity<>(new DefaultError(ex.getMessage()), HttpStatus.NOT_FOUND);
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
