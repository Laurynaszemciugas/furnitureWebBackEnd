package com.example.jwt_demo.GlobalExseptions.Handler;

import com.example.jwt_demo.GlobalExseptions.ValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handleOrderValidation(ValidationException ex){
        return ResponseEntity.badRequest().body(ex.getMessage());
    }


}
