package com.example.jwt_demo.controller;

import com.example.jwt_demo.Common.ErrorResponse;
import com.example.jwt_demo.Common.Logic;
import com.example.jwt_demo.DTOS.WorkDay.WorkDayMiniStats;
import com.example.jwt_demo.Entity.Employee;
import com.example.jwt_demo.Entity.User;
import com.example.jwt_demo.Entity.WorkDay;
import com.example.jwt_demo.Entity.WorkDone;
import com.example.jwt_demo.Enums.Warnings;
import com.example.jwt_demo.GlobalExseptions.Exseptions.ValidationException;
import com.example.jwt_demo.repository.EmployeeRepository;
import com.example.jwt_demo.repository.UserRepository;
import com.example.jwt_demo.repository.WorkDayRepository;
import com.example.jwt_demo.repository.WorkDoneRepository;
import com.example.jwt_demo.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/WorkDone")
public class WorkDoneController {


    @Autowired
    Common common;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    WorkDayRepository workDayRepository;

    @Autowired
    WorkDoneRepository workDoneRepository;

    @Autowired
    Logic logic;



    @GetMapping("/getQuickActionWorkHoursMiniStats/{employeeId}")
    public ResponseEntity<WorkDayMiniStats> getQuickActionWorkHoursMiniStats(@PathVariable Long employeeId){


        CustomUserDetails user = common.getUserData();

        Employee employee = employeeRepository.findById(employeeId).orElseThrow();


        if(!user.getId().equals(employee.getUser().getId())){
            throw new ValidationException("Something wend wrong",Warnings.ERROR);
        }




        return ResponseEntity.ok(workDoneRepository.workDayMini(employeeId));

    }



    @GetMapping("/allInfoAccordingToEmployee/{employeeId}/{from}/{to}/{page}")
    public ResponseEntity<List<WorkDay>> allInfoAccordingToEmployee(@PathVariable Long employeeId, @PathVariable LocalDate from, @PathVariable LocalDate to, @PathVariable int page){


        CustomUserDetails user = common.getUserData();

        Employee employee = employeeRepository.findById(employeeId).orElseThrow();


        if(!user.getId().equals(employee.getUser().getId())){
            throw new ValidationException("Something wend wrong",Warnings.ERROR);
        }




        return ResponseEntity.ok(workDoneRepository.allInfoAccordingToEmployee(employeeId,logic.dateConverter(from),logic.dateConverter(to), PageRequest.of(page,10)));

    }


    @GetMapping("/getTotalPagesInQuickActions/{employeeId}/{from}/{to}")
    public ResponseEntity<Long> getTotalPagesInQuickActions(@PathVariable Long employeeId, @PathVariable LocalDate from, @PathVariable LocalDate to){


        CustomUserDetails user = common.getUserData();

        Employee employee = employeeRepository.findById(employeeId).orElseThrow();


        if(!user.getId().equals(employee.getUser().getId())){
            throw new ValidationException("Something wend wrong",Warnings.ERROR);
        }




        return ResponseEntity.ok(workDoneRepository.getTotalPagesInQuickActions(employeeId,logic.dateConverter(from),logic.dateConverter(to)));

    }



}
