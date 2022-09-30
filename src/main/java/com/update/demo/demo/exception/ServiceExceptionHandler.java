package com.update.demo.demo.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

//@ControllerAdvice
public class ServiceExceptionHandler {
    Logger logger = LoggerFactory.getLogger(ServiceExceptionHandler.class);

    @ExceptionHandler(value = {Exception.class})
    protected ResponseEntity<Object> handleException(Exception ex) {
        logger.error("An Error has occurred, ", ex);
        return ResponseEntity.internalServerError().body(ex.getMessage());
    }
}