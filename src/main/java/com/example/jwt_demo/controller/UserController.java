package com.example.jwt_demo.controller;

import com.example.jwt_demo.Common.ErrorResponse;
import com.example.jwt_demo.DTOS.User.AccountOverview;
import com.example.jwt_demo.DTOS.User.Appearance;
import com.example.jwt_demo.DTOS.User.PersonalPrefrences;
import com.example.jwt_demo.DTOS.User.ProfileInformation;
import com.example.jwt_demo.Entity.User;
import com.example.jwt_demo.Entity.UserSettings;
import com.example.jwt_demo.Enums.Warnings;
import com.example.jwt_demo.GlobalExseptions.Exseptions.ValidationException;
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
        existingUser.setImageUrl(userData.getImageUrl());

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

    @GetMapping("/getAppearance")
    public ResponseEntity<Appearance> getAppearance(){

        CustomUserDetails user = common.getUserData();

        return ResponseEntity.ok(userRepository.getAppearance(user.getId()));

    }


    @GetMapping("/saveTheme/{value}")
    public ResponseEntity<ErrorResponse> saveTheme(@PathVariable String value){

        CustomUserDetails user = common.getUserData();

        User user1 = userRepository.findById(user.getId()).orElseThrow();
        UserSettings userSettings = user1.getUserSettingsList();


//        if(!value.equals("Light") || !value.equals("Dark")){
//            return ResponseEntity.ok(new ErrorResponse("Fail",Warnings.ERROR));
//        }

        userSettings.setTheme(value);

        userRepository.save(user1);


        return ResponseEntity.ok(new ErrorResponse("Successfully changed theme",Warnings.OK));

    }

    @GetMapping("/saveAccent/{value}")
    public ResponseEntity<ErrorResponse> saveAccent(@PathVariable String value){

        CustomUserDetails user = common.getUserData();

        User user1 = userRepository.findById(user.getId()).orElseThrow();
        UserSettings userSettings = user1.getUserSettingsList();


//        if(value != "Light" || value != "Dark"){
//            return ResponseEntity.ok(new ErrorResponse("Fail",Warnings.ERROR));
//        }

        userSettings.setAccent(value);

        userRepository.save(user1);


        return ResponseEntity.ok(new ErrorResponse("Successfully changed accent",Warnings.OK));

    }

    @GetMapping("/saveSidebar/{value}")
    public ResponseEntity<ErrorResponse> saveSidebar (@PathVariable String value){

        CustomUserDetails user = common.getUserData();

        User user1 = userRepository.findById(user.getId()).orElseThrow();
        UserSettings userSettings = user1.getUserSettingsList();


//        if(value != "Large" || value != "Small"){
//            return ResponseEntity.ok(new ErrorResponse("Fail",Warnings.ERROR));
//        }

        userSettings.setSidebarSize(value);

        userRepository.save(user1);


        return ResponseEntity.ok(new ErrorResponse("Successfully changed sidebar",Warnings.OK));

    }





}
