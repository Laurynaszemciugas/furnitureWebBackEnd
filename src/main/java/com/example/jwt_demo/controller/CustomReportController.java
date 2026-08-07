package com.example.jwt_demo.controller;

import com.example.jwt_demo.Common.ErrorResponse;
import com.example.jwt_demo.DTOS.CustomReport.CustomReportFeed;
import com.example.jwt_demo.Entity.CreateReport.Report;
import com.example.jwt_demo.Entity.CreateReport.ReportItems;
import com.example.jwt_demo.Enums.Warnings;
import com.example.jwt_demo.repository.CustomReportRepository;
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


    @PostMapping("/createNewCustomReport")
    public ResponseEntity<ErrorResponse> addNewReport(@RequestBody Report report){




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




        return ResponseEntity.ok(customReportRepository.customRepostFeedList());
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
