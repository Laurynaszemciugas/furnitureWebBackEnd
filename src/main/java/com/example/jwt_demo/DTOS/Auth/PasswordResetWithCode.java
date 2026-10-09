package com.example.jwt_demo.DTOS.Auth;

import com.example.jwt_demo.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetWithCode {

    private String code;
    private String password;
    private String reEnterPassword;


}
