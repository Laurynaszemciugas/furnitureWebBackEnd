package com.example.jwt_demo.controller;

import com.example.jwt_demo.Common.ErrorResponse;
import com.example.jwt_demo.DTOS.CustomReport.CustomReportFeed;
import com.example.jwt_demo.Entity.CreateReport.Report;
import com.example.jwt_demo.Enums.Warnings;
import com.example.jwt_demo.repository.CustomReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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


    @GetMapping("/getCustomReportFeed")
    public ResponseEntity<List<CustomReportFeed>> getCustomReportFeed(){




        return ResponseEntity.ok(customReportRepository.customRepostFeedList());
    }

    @GetMapping("/getReportAccordingToId/{id}")
    public ResponseEntity<Report> getReportAccordingToId(@PathVariable Long id){




        return ResponseEntity.ok(customReportRepository.findById(id).orElseThrow());
    }


}
