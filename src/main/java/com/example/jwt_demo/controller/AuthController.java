package com.example.jwt_demo.controller;

import com.example.jwt_demo.Enums.AccountStatus;
import com.example.jwt_demo.Enums.Role;
import com.example.jwt_demo.Entity.User;
import com.example.jwt_demo.repository.UserRepository;
import com.example.jwt_demo.security.CustomUserDetails;
import com.example.jwt_demo.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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
    public String authenticateUser(@RequestBody User user) {

        System.out.println("here");
        System.out.println(user.getGmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getGmail(),
                        user.getPassword()
                )
        );
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();



        return jwtUtils.generateToken(userDetails);
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
            return "Error: Username is already taken!";
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
        null);
        userRepository.save(newUser);
        return "User registered successfully!";
    }
}
