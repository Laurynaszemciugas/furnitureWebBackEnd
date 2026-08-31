package com.example.jwt_demo.controller;

import com.example.jwt_demo.Common.ErrorResponse;
import com.example.jwt_demo.Common.Logic;
import com.example.jwt_demo.Common.ProvidedDataChecker;
import com.example.jwt_demo.DTOS.ActionLogs.ActionLogFeed;
import com.example.jwt_demo.DTOS.Order.OrdersFeedData;
import com.example.jwt_demo.Entity.ActionTracker;
import com.example.jwt_demo.Entity.CreateReport.Report;
import com.example.jwt_demo.Enums.Warnings;
import com.example.jwt_demo.FilterDTO.ActionLog.ActionLogFilterHolder;
import com.example.jwt_demo.FilterDTO.Order.OrderFilterHolder;
import com.example.jwt_demo.repository.ActionTrackerRepository;
import com.example.jwt_demo.security.CustomUserDetails;
import org.apache.juli.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ActionTracker")
public class ActionTrackerController {


    @Autowired
    ActionTrackerRepository actionTrackerRepository;

    @Autowired
    ProvidedDataChecker providedDataChecker;

    @Autowired
    Common common;

    @Autowired
    Logic logic;

    @PostMapping("/addAction")
    public void addNewReport(@RequestBody ActionTracker actionTracker){


        actionTrackerRepository.save(actionTracker);


    }


    @PostMapping("/getActionLogFeed")
    public ResponseEntity<List<ActionLogFeed>> getActionLogFeed(@RequestBody ActionLogFilterHolder actionLogFilterHolder) {

        CustomUserDetails user = common.getUserData();

        actionLogFilterHolder = providedDataChecker.defaultValueChecker(actionLogFilterHolder, ActionLogFilterHolder.class);

        return ResponseEntity.ok(
                actionTrackerRepository.getActionLogFeed(
                        actionLogFilterHolder.getPromt(),
                        actionLogFilterHolder.getWhoMadeTheAction(),
                        actionLogFilterHolder.getActionType(),
                        logic.dateConverter(actionLogFilterHolder.getDateFrom()),
                        logic.dateConverter(actionLogFilterHolder.getDateTo()),
                        PageRequest.of(actionLogFilterHolder.getPage(), actionLogFilterHolder.getPageCount()),
                        user.getId()
                )
        );
    }


    @PostMapping("/getAmountOfPages")
    public ResponseEntity<Long> getAmountOfPages(@RequestBody ActionLogFilterHolder actionLogFilterHolder) {

        CustomUserDetails user = common.getUserData();

        actionLogFilterHolder = providedDataChecker.defaultValueChecker(actionLogFilterHolder, ActionLogFilterHolder.class);

        Long count = actionTrackerRepository.getNumberOfOrderPages(
                actionLogFilterHolder.getPromt(),
                actionLogFilterHolder.getWhoMadeTheAction(),
                actionLogFilterHolder.getActionType(),
                logic.dateConverter(actionLogFilterHolder.getDateFrom()),
                logic.dateConverter(actionLogFilterHolder.getDateTo()),
                Double.valueOf(actionLogFilterHolder.getPageCount()),
                user.getId());


        return ResponseEntity.ok(
                count
        );
    }




}
