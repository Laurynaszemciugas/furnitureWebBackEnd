package com.example.jwt_demo.controller;

import com.example.jwt_demo.Common.ErrorResponse;
import com.example.jwt_demo.Entity.CreateReport.Report;
import com.example.jwt_demo.Enums.Warnings;
import com.example.jwt_demo.repository.CustomReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customReport")
public class CustomReportController {


    @Autowired
    CustomReportRepository customReportRepository;


    @PostMapping("/createNewCustomReport")
    public ResponseEntity<ErrorResponse> addNewReport(@RequestBody Report report){


        for(var s : report.getReportItemsList()){
            System.out.println(s.getWidth());
        }

        customReportRepository.save(report);


        return ResponseEntity.ok(new ErrorResponse("zaza", Warnings.OK));
    }



}
