package com.example.jwt_demo.controller;

import com.example.jwt_demo.Common.ErrorResponse;
import com.example.jwt_demo.Entity.Employee;
import com.example.jwt_demo.Entity.User;
import com.example.jwt_demo.Entity.WorkDay;
import com.example.jwt_demo.Enums.Warnings;
import com.example.jwt_demo.repository.EmployeeRepository;
import com.example.jwt_demo.repository.UserRepository;
import com.example.jwt_demo.repository.WorkDayRepository;
import com.example.jwt_demo.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/WorkDay")
public class WorkDayController {


    @Autowired
    Common common;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    WorkDayRepository workDayRepository;

    @GetMapping("/addWorkDay")
    public ResponseEntity<ErrorResponse> addWorkDay(){


        CustomUserDetails user = common.getUserData();

        Long employeeId = employeeRepository.employeeId(user.getId());

        LocalDate today = LocalDate.now();

        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);

//        if(workDayRepository.doesEmployeeHadWorkDayToday(employeeId,start,end) == 1){
//            return ResponseEntity.ok(new ErrorResponse("You already had worked day cannot get another please try again tomorrow ", Warnings.WARNING));
//        }

        if(workDayRepository.doesEmployeeAlreadyStartedWork(employeeId) == 1){

           WorkDay workDay = workDayRepository.getWorkDayInfo(employeeId);

           workDay.setWorkDayEnd(LocalDateTime.now());


           Long totalMinutes = Duration.between(workDay.getWorkDayCreated(),LocalDateTime.now()).toMinutes();

           Long hours = totalMinutes / 60;
           Long minutes = totalMinutes % 60;

           workDay.setWorkedForMinutes(totalMinutes);

           workDayRepository.save(workDay);


            return ResponseEntity.ok(new ErrorResponse("Work day is ended you worked for " + String.format("%sh %sm",hours,minutes), Warnings.OK));
        }
        else {

            Employee employee = employeeRepository.findById(employeeId).orElseThrow();

            Long userId = employee.getUser().getId();

            User user1 = userRepository.findById(userId).orElseThrow();

            WorkDay workDay = new WorkDay();
            workDay.setEmployee(employee);
            workDay.setUser(user1);


            workDayRepository.save(workDay);

        }

        return ResponseEntity.ok(new ErrorResponse("Bet", Warnings.OK));

    }


    @GetMapping("/getWorkDayInfo")
    public ResponseEntity<WorkDay> getWorkDayInfo(){


        CustomUserDetails user = common.getUserData();

        Long employeeId = employeeRepository.employeeId(user.getId());

        WorkDay workDay = workDayRepository.getWorkDayInfo(employeeId);

        return ResponseEntity.ok(workDay == null ? new WorkDay() : workDay);

    }

}
