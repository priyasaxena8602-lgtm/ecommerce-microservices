package com.priyasaxena.ecommerce.productservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFoundException(ProductNotFoundException e){
        ErrorResponse response = new ErrorResponse(LocalDateTime.now(), e.getMessage(), "Product not found!");

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}
