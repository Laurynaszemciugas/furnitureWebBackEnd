package com.example.jwt_demo.GlobalExseptions.Handler;

import com.example.jwt_demo.Common.ErrorResponse;
import com.example.jwt_demo.Enums.Warnings;
import com.example.jwt_demo.GlobalExseptions.Exseptions.ValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {




    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleOrderValidation(ValidationException ex) {

        return ResponseEntity.badRequest().body(
                new ErrorResponse(
                        ex.getMessage(),
                        ex.getWarning()
                )
        );
    }


}
