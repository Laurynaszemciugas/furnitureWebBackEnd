package com.example.jwt_demo.controller;

import com.example.jwt_demo.repository.MaterialRepository;
import com.example.jwt_demo.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/material")
public class MaterialController {

    @Autowired
    MaterialRepository materialRepository;

    @GetMapping("/getMaterialNames")
    public ResponseEntity<List<String>> getMaterialNames(){

        CustomUserDetails user =
                (CustomUserDetails) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        System.out.println(user.getUsername());

        return ResponseEntity.ok(materialRepository.getAllMaterialNames(user.getId()));


    }


}
