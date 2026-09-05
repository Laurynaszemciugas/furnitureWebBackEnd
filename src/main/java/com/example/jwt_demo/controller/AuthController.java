package com.example.jwt_demo.controller;

import com.example.jwt_demo.Common.ErrorResponse;
import com.example.jwt_demo.Common.GoogleTokenVerifier;
import com.example.jwt_demo.Entity.Authenfication.GmailAuth;
import com.example.jwt_demo.Entity.UserSettings;
import com.example.jwt_demo.Enums.AccountStatus;
import com.example.jwt_demo.Enums.Role;
import com.example.jwt_demo.Entity.User;
import com.example.jwt_demo.Enums.Verification;
import com.example.jwt_demo.Enums.Warnings;
import com.example.jwt_demo.GlobalExseptions.Exseptions.ValidationException;
import com.example.jwt_demo.repository.GmailVerificationRepository;
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
import java.util.Random;
import java.util.concurrent.CompletableFuture;

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

    @Autowired
    EmailSenderContoller emailSenderContoller;

    @Autowired
    GmailVerificationRepository gmailVerificationRepository;


    @PostMapping("/google")
    public ResponseEntity<?> authenticateUserGoogle(@RequestBody String googleToken, HttpServletRequest request) throws GeneralSecurityException, IOException {


        System.out.println("1");

        googleToken = googleToken.replace("\"", "");

        System.out.println("2");

        String ip = request.getRemoteAddr();

        System.out.println("3");

        System.out.println(googleToken);

        System.out.println("4");

        GoogleIdToken.Payload payload = googleTokenVerifier.verify(googleToken);

        System.out.println("5");

        String googleId = payload.getSubject();
        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String lastName = (String) payload.get("family_name");
        String picture = (String) payload.get("picture");
        Boolean emailVerified = payload.getEmailVerified();

        System.out.println("6");



        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            emailSenderContoller.welcomeMessage(email);
            System.out.println("gmail sendd");
        });

        System.out.println("7");

        System.out.println("Google ID: " + googleId);
        System.out.println("Email: " + email);
        System.out.println("Name: " + name);
        System.out.println("Picture: " + picture);

        System.out.println("8");

        // find if account doesnt exists

        if(userRepository.existsByGmail(email)){

            System.out.println("9");

            // check if ids match
            if(googleId.equals(userRepository.findGoogleId(email))){

                System.out.println("10");

                User user = userRepository.findByGmail(email);

                CustomUserDetails userDetails = new CustomUserDetails();
                userDetails.setUsername(user.getGmail());
                userDetails.setId(user.getId());
                userDetails.setEmail(user.getGmail());
                userDetails.setPassword(user.getPassword());
                userDetails.setRole(user.getRole());

                System.out.println("10");

                return ResponseEntity.ok(new ErrorResponse(jwtUtils.generateToken(userDetails),Warnings.OK));

            }


        }
        else{
            UserSettings userSettings = new UserSettings();

            System.out.println("11");

            // Create new user's account
            User newUser =
                    new User(
                            null,
                            email,
                            name,
                            lastName,
                            null,
                            "",
                            Role.USER,
                            AccountStatus.ALLOWED,
                            null,
                            LocalDateTime.now(),
                            null,
                            null,
                            googleId,
                            emailVerified ? Verification.VERIFIED : Verification.NOT_VERIFIED,
                            null,
                            ip,
                            name + " " + lastName,
                            picture,
                            userSettings);

            userSettings.setUser(newUser);

            userRepository.save(newUser);
            return ResponseEntity.ok(new ErrorResponse("Account was created successfully",Warnings.OK));
        }





    return ResponseEntity.ok(new ErrorResponse("Something went wrong",Warnings.WARNING));

    }

    @PostMapping("/signin")
    public ResponseEntity<ErrorResponse> authenticateUser(@RequestBody User user) {



        User userForCheck = userRepository.findByGmail(user.getGmail());

        if(userRepository.findGoogleId(user.getGmail()) != null){
            throw new ValidationException(
                    "This gmail is already in use",
                    Warnings.ERROR);
        }


        if(userForCheck.getVerification() == Verification.NOT_VERIFIED){
            throw new ValidationException(
                    "Gmail is not verified please go to your gmail and verify it or just use Google sign in",
                    Warnings.ERROR);
        }

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



        Random random = new Random();

        long number = 100_000_000_000_000L + (long)(random.nextDouble() * 900_000_000_000_000L);





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
                        Verification.NOT_VERIFIED,
                        null,
                        ip,
                user.getName() + " " + user.getLastName(),
                user.getImageUrl() == null ? "No_picture.png" : user.getImageUrl(),
                        userSettings);

        userSettings.setUser(newUser);

        GmailAuth gmailAuth = new GmailAuth();
        gmailAuth.setOneTimeCode(String.valueOf(number));
        gmailAuth.setUser(newUser);


        userRepository.save(newUser);
        gmailVerificationRepository.save(gmailAuth);
        emailSenderContoller.verificationGmail(newUser.getGmail(),String.valueOf(number));

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

    @PostMapping("/verifyGmail")
    public ResponseEntity<ErrorResponse> gmailVerification(@RequestBody String code){
        code = code.replace("\"","");
        if(!gmailVerificationRepository.existsByOneTimeCode(code)){

            System.out.println(code);
            return ResponseEntity.ok(new ErrorResponse("Code doesnt match", Warnings.ERROR));
        }

        else{
            GmailAuth gmailAuth = gmailVerificationRepository.findByOneTimeCode(code);
            User user = userRepository.findByGmail(gmailAuth.getUser().getGmail());

            user.setVerification(Verification.VERIFIED);

            userRepository.save(user);

            gmailVerificationRepository.delete(gmailAuth);
            return ResponseEntity.ok(new ErrorResponse("Success gmail verified", Warnings.OK));

        }




    }

}




