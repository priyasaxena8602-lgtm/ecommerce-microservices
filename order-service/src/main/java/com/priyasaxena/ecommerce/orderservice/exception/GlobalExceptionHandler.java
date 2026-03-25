package com.priyasaxena.ecommerce.orderservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFoundException(OrderNotFoundException e){
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), e.getMessage(), "Order not found!");

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

    }
}
