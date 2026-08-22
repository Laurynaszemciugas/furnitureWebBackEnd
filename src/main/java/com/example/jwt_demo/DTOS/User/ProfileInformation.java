package com.example.jwt_demo.DTOS.User;

import com.example.jwt_demo.Enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileInformation {

    private String fullName;
    private String emailAddress;
    private Role role;
    private String phoneNumber;
    private String bio;
    private String imageUrl;

}
