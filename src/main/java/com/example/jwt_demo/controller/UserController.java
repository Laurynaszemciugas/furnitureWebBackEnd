package com.example.jwt_demo.controller;

import com.example.jwt_demo.Common.ErrorResponse;
import com.example.jwt_demo.DTOS.User.AccountOverview;
import com.example.jwt_demo.DTOS.User.PersonalPrefrences;
import com.example.jwt_demo.DTOS.User.ProfileInformation;
import com.example.jwt_demo.Entity.User;
import com.example.jwt_demo.Entity.UserSettings;
import com.example.jwt_demo.Enums.Warnings;
import com.example.jwt_demo.repository.UserRepository;
import com.example.jwt_demo.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {


    @Autowired
    Common common;

    @Autowired
    UserRepository userRepository;


    @GetMapping("/getProfileInfo")
    public ResponseEntity<ProfileInformation> getProfileInfo(){

        CustomUserDetails user = common.getUserData();


        return ResponseEntity.ok(userRepository.getProfileInfo(user.getId()));

    }

    @PostMapping("/saveProfileInfo")
    public ResponseEntity<ErrorResponse> saveProfileInfo(@RequestBody User userData){

        CustomUserDetails user = common.getUserData();

        User existingUser = userRepository.findById(user.getId()).orElseThrow();

        existingUser.setPhoneNumber(userData.getPhoneNumber());
        existingUser.setBio(userData.getBio());

        userRepository.save(existingUser);


        return ResponseEntity.ok(new com.example.jwt_demo.Common.ErrorResponse("User settings updated",Warnings.OK));

    }


    @GetMapping("/getAccountOverview")
    public ResponseEntity<AccountOverview> getAccountOverview(){

        CustomUserDetails user = common.getUserData();


        return ResponseEntity.ok(userRepository.getAccountOverview(user.getId()));

    }

    @GetMapping("/getPersonalPrefrences")
    public ResponseEntity<PersonalPrefrences> getPersonalPrefrences(){

        CustomUserDetails user = common.getUserData();


        return ResponseEntity.ok(userRepository.getPersonalPrefrences(user.getId()));

    }

    @GetMapping("/getUserSettings")
    public ResponseEntity<UserSettings> getUserSettings(){

        CustomUserDetails user = common.getUserData();

        return ResponseEntity.ok(userRepository.getUserSettings(user.getId()));

    }

    @PostMapping("/savePersonalPrefrences")
    public ResponseEntity<ErrorResponse> savePersonalPrefrences(@RequestBody User userData){

        CustomUserDetails user = common.getUserData();

        User existingUser = userRepository.findById(user.getId()).orElseThrow();

        UserSettings userSettings = existingUser.getUserSettingsList();

        userSettings.setLanguage(userData.getUserSettingsList().getLanguage());
        userSettings.setDateFormat(userData.getUserSettingsList().getDateFormat());
        userSettings.setReceiveGmail(userData.getUserSettingsList().isReceiveGmail());
        userSettings.setTimeZone(userData.getUserSettingsList().getTimeZone());

        userRepository.save(existingUser);


        return ResponseEntity.ok(new com.example.jwt_demo.Common.ErrorResponse("Personal preferences updated",Warnings.OK));

    }





}
