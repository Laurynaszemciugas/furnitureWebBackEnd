package com.example.jwt_demo.Common;

import com.example.jwt_demo.Enums.Warnings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

            String message;
            Warnings warning;
}
