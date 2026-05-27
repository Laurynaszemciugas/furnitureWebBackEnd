package com.example.jwt_demo.controller;

import com.example.jwt_demo.DTOS.Order.ComboBoxEmployees;
import com.example.jwt_demo.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    @Autowired
    EmployeeRepository employeeRepository;

    @GetMapping("/getMiniEmployeeData")
    public ResponseEntity<List<ComboBoxEmployees>> getMiniEmployeeData(){
        return ResponseEntity.ok(employeeRepository.getUserEmployees());
    }

}
