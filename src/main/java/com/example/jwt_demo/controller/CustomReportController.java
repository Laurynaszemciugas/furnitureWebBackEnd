package com.example.jwt_demo.controller;

import com.example.jwt_demo.Common.ErrorResponse;
import com.example.jwt_demo.DTOS.CustomReport.CustomReportFeed;
import com.example.jwt_demo.Entity.CreateReport.Report;
import com.example.jwt_demo.Entity.CreateReport.ReportItems;
import com.example.jwt_demo.Enums.Warnings;
import com.example.jwt_demo.repository.CustomReportRepository;
import com.example.jwt_demo.repository.UserRepository;
import com.example.jwt_demo.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/customReport")
public class CustomReportController {


    @Autowired
    CustomReportRepository customReportRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    Common common;


    @PostMapping("/createNewCustomReport")
    public ResponseEntity<ErrorResponse> addNewReport(@RequestBody Report report){

        CustomUserDetails user = common.getUserData();

        report.setUser(userRepository.findById(user.getId()).orElseThrow());


        customReportRepository.save(report);


        return ResponseEntity.ok(new ErrorResponse(report.getReportName() + " was created successfully", Warnings.OK));
    }


    @PostMapping("/editCustomReport")
    public ResponseEntity<ErrorResponse> editCustomReport(@RequestBody Report report){

        Report existingReport = customReportRepository.findById(report.getId()).orElseThrow();

        existingReport.setReportColor(report.getReportColor());
        existingReport.setReportCategory(report.getReportCategory());
        existingReport.setReportName(report.getReportName());
        existingReport.setDescription(report.getDescription());
        existingReport.setDashboardWidget(report.getDashboardWidget());

        List<ReportItems> reportItemsList = new ArrayList<>();

        existingReport.getReportItemsList().clear();

        for (ReportItems item : report.getReportItemsList()) {
            item.setReport(existingReport); // important for bidirectional relation
            existingReport.getReportItemsList().add(item);
        }


        customReportRepository.save(existingReport);


        return ResponseEntity.ok(new ErrorResponse(existingReport.getReportName() + " was edited successfully", Warnings.OK));
    }


    @GetMapping("/getCustomReportFeed")
    public ResponseEntity<List<CustomReportFeed>> getCustomReportFeed(){


        CustomUserDetails user = common.getUserData();

        return ResponseEntity.ok(customReportRepository.customRepostFeedList(user.getId()));
    }

    @GetMapping("/getReportAccordingToId/{id}")
    public ResponseEntity<Report> getReportAccordingToId(@PathVariable Long id){




        return ResponseEntity.ok(customReportRepository.findById(id).orElseThrow());
    }

    @GetMapping("/deleteCustomReport/{id}")
    public ResponseEntity<ErrorResponse> deleteCustomReport(@PathVariable Long id){


        Report report = customReportRepository.findById(id).orElseThrow();

        customReportRepository.deleteById(id);




        return ResponseEntity.ok(new ErrorResponse(report.getReportName() + " was deleted successfully",Warnings.OK));
    }


}
