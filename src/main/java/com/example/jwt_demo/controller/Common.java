package com.example.jwt_demo.controller;

import com.example.jwt_demo.security.CustomUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class Common {

    public CustomUserDetails getUserData(){

        try {
            CustomUserDetails user =
                    (CustomUserDetails) SecurityContextHolder
                            .getContext()
                            .getAuthentication()
                            .getPrincipal();
            return user;
        } catch (Exception e) {
            return null;
        }
    }

}
