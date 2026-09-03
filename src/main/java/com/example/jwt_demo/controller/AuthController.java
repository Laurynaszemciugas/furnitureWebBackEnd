package com.example.jwt_demo.controller;

import com.example.jwt_demo.Common.ErrorResponse;
import com.example.jwt_demo.Common.GoogleTokenVerifier;
import com.example.jwt_demo.Entity.UserSettings;
import com.example.jwt_demo.Enums.AccountStatus;
import com.example.jwt_demo.Enums.Role;
import com.example.jwt_demo.Entity.User;
import com.example.jwt_demo.Enums.Warnings;
import com.example.jwt_demo.GlobalExseptions.Exseptions.ValidationException;
import com.example.jwt_demo.repository.UserRepository;
import com.example.jwt_demo.security.CustomUserDetails;
import com.example.jwt_demo.security.JwtUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
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

    @Autowired
    GoogleTokenVerifier googleTokenVerifier;



    @PostMapping("/google")
    public ResponseEntity<ErrorResponse> authenticateUserGoogle(@RequestBody String googleToken) throws GeneralSecurityException, IOException {

        googleToken = googleToken.replace("\"", "");

        System.out.println(googleToken);

        GoogleIdToken.Payload payload = googleTokenVerifier.verify(googleToken);

        String googleId = payload.getSubject();
        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String picture = (String) payload.get("picture");

        System.out.println("Google ID: " + googleId);
        System.out.println("Email: " + email);
        System.out.println("Name: " + name);
        System.out.println("Picture: " + picture);

        // find if account doesnt exists






    return ResponseEntity.ok(new ErrorResponse("doing stuff",Warnings.OK));

    }

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

            User user1 = userRepository.findByGmail(user.getGmail());

            user1.setLastLogin(LocalDateTime.now());

            userRepository.save(user1);

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
    public String registerUser(@RequestBody User user, HttpServletRequest request) {
        if (userRepository.existsByGmail(user.getGmail())) {
            throw  new ValidationException("Gmail is already taken!", Warnings.ERROR);
        }

        String ip = request.getRemoteAddr();

        UserSettings userSettings = new UserSettings();

        // Create new user's account
        User newUser =
                new User(
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
                null,
                null,
                        null,
                        null,
                        null,
                        ip,
                user.getName() + " " + user.getLastName(),
                user.getImageUrl() == null ? "No_picture.png" : user.getImageUrl(),
                        userSettings);

        userSettings.setUser(newUser);

        userRepository.save(newUser);
        return "User registered successfully!";
    }


    public String systemRegister(User user) {
        if (userRepository.existsByGmail(user.getGmail())) {
            throw  new ValidationException("Gmail is already taken!", Warnings.ERROR);
        }

        UserSettings userSettings = new UserSettings();

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
                null,
                null,
                null,
                null,
                null,
                null,
                user.getName() + " " + user.getLastName(),
                user.getImageUrl() == null ? "No_picture.png" : user.getImageUrl(),
                userSettings);

        userSettings.setUser(newUser);

        userRepository.save(newUser);


        return "User registered successfully!";
    }

}




