package com.example.jwt_demo.DTOS.User;

import com.example.jwt_demo.Enums.Verification;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountOverview {


    private LocalDateTime created;
    private String gmail;
    private Verification verification;
    private LocalDateTime bannedTill;
    private LocalDateTime lastLogin;
    private String ip;


}
