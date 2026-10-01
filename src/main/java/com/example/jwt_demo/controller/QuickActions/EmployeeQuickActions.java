package com.example.jwt_demo.controller.QuickActions;

import com.example.jwt_demo.Common.ErrorResponse;
import com.example.jwt_demo.Entity.Employee;
import com.example.jwt_demo.Entity.Materials;
import com.example.jwt_demo.Enums.EmployeeAcIn;
import com.example.jwt_demo.Enums.Warnings;
import com.example.jwt_demo.GlobalExseptions.Exseptions.ValidationException;
import com.example.jwt_demo.controller.Common;
import com.example.jwt_demo.repository.EmployeeRepository;
import com.example.jwt_demo.repository.MaterialRepository;
import com.example.jwt_demo.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/employeeQuickActions")
public class EmployeeQuickActions {

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    Common common;

    @GetMapping("/updateEmployeeActiveStatus/{employeeId}/{toggleStatus}")
    public ResponseEntity<ErrorResponse> updateEmployeeActiveStatus(@PathVariable Long employeeId,@PathVariable EmployeeAcIn toggleStatus) {

        CustomUserDetails user = common.getUserData();

        Employee employee = employeeRepository.findById(employeeId).orElseThrow();

        if(!user.getId().equals(employee.getUser().getId())){
            throw new ValidationException("Something went wrong",Warnings.ERROR);
        }

        employee.setEmployeeAcIn(toggleStatus);

        employeeRepository.save(employee);



        return ResponseEntity.ok(new ErrorResponse("Employee updated", Warnings.OK));

    }




}
