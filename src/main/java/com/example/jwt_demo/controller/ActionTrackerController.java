package com.example.jwt_demo.controller;

import com.example.jwt_demo.Common.ErrorResponse;
import com.example.jwt_demo.Entity.ActionTracker;
import com.example.jwt_demo.Entity.CreateReport.Report;
import com.example.jwt_demo.Enums.Warnings;
import com.example.jwt_demo.repository.ActionTrackerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ActionTracker")
public class ActionTrackerController {


    @Autowired
    ActionTrackerRepository actionTrackerRepository;

    @PostMapping("/addAction")
    public void addNewReport(@RequestBody ActionTracker actionTracker){


        actionTrackerRepository.save(actionTracker);


    }



}
