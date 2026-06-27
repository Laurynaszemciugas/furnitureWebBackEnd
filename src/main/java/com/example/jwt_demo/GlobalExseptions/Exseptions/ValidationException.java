package com.example.jwt_demo.GlobalExseptions.Exseptions;

import com.example.jwt_demo.Enums.Warnings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ValidationException extends RuntimeException {

    private String message;
    private Warnings warning;

}
