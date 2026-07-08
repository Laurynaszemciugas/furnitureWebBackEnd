package com.example.jwt_demo.controller;

import com.example.jwt_demo.Common.ErrorResponse;
import com.example.jwt_demo.Enums.AccountStatus;
import com.example.jwt_demo.Enums.Role;
import com.example.jwt_demo.Entity.User;
import com.example.jwt_demo.Enums.Warnings;
import com.example.jwt_demo.GlobalExseptions.Exseptions.ValidationException;
import com.example.jwt_demo.repository.UserRepository;
import com.example.jwt_demo.security.CustomUserDetails;
import com.example.jwt_demo.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtUtil jwtUtils;

    @Autowired
    Common common;

    @PostMapping("/signin")
    public ResponseEntity<ErrorResponse> authenticateUser(@RequestBody User user) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getGmail(),
                            user.getPassword()
                    )
            );
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            return ResponseEntity.ok(new ErrorResponse(jwtUtils.generateToken(userDetails),Warnings.OK));



        } catch (Exception e) {
            throw new ValidationException(
                    "Password or the login name is incorrect",
                    Warnings.ERROR
            );
        }
    }

    @GetMapping("/profile")
    public String profile() {

        CustomUserDetails user = common.getUserData();

        return "ID: " + user.getId() +
                ", Gmail: " + user.getUsername() +
                ", Role: " + user.getRole();
    }


    @PostMapping("/signup")
    public String registerUser(@RequestBody User user) {
        if (userRepository.existsByGmail(user.getGmail())) {
            throw  new ValidationException("Gmail is already taken!", Warnings.ERROR);
        }
        // Create new user's account
        User newUser = new User(
                null,
                user.getGmail(),
                user.getName(),
                user.getLastName(),
                encoder.encode(user.getPassword()),
                "",
                Role.USER,
                AccountStatus.ALLOWED,
                null,
                LocalDateTime.now(),
                user.getName() + " " + user.getLastName(),
                user.getImageUrl() == null ? "No_picture.png" : user.getImageUrl());
        userRepository.save(newUser);
        return "User registered successfully!";
    }


    public String systemRegister(User user) {
        if (userRepository.existsByGmail(user.getGmail())) {
            throw  new ValidationException("Gmail is already taken!", Warnings.ERROR);
        }
        // Create new user's account
        User newUser = new User(
                null,
                user.getGmail(),
                user.getName(),
                user.getLastName(),
                encoder.encode(user.getPassword()),
                user.getRecoveryPin(),
                user.getRole(),
                AccountStatus.ALLOWED,
                null,
                LocalDateTime.now(),
                user.getName() + " " + user.getLastName(),
                user.getImageUrl() == null ? "No_picture.png" : user.getImageUrl());
        userRepository.save(newUser);


        return "User registered successfully!";
    }

}




