package com.example.jwt_demo.GlobalExseptions.Exseptions;

import com.example.jwt_demo.Enums.Warnings;

public class ValidationException extends RuntimeException {

    private String message;
    private Warnings warning;

    public ValidationException(String message, Warnings warning) {
        this.message = message;
        this.warning = warning;
    }

    public Warnings getWarning() {
        return warning;
    }

    public String getMessage() {
        return message;
    }
}
