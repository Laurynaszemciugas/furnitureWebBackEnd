package com.example.jwt_demo.controller;

import com.example.jwt_demo.Common.Logic;
import com.example.jwt_demo.Common.ProvidedDataChecker;
import com.example.jwt_demo.DTOS.Common.MiniStatHolder;
import com.example.jwt_demo.DTOS.Employees.EmployeeBriefDto;
import com.example.jwt_demo.DTOS.Order.ComboBoxEmployees;
import com.example.jwt_demo.FilterDTO.Employee.EmployeeFilterHolder;
import com.example.jwt_demo.FilterDTO.Material.MaterialFilterHolder;
import com.example.jwt_demo.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    ProvidedDataChecker providedDataChecker;

    @Autowired
    Logic logic;

    @GetMapping("/getMiniEmployeeData")
    public ResponseEntity<List<ComboBoxEmployees>> getMiniEmployeeData(){
        return ResponseEntity.ok(employeeRepository.getUserEmployees());
    }

    @GetMapping("/getEmployeeeMiniStats/{fromDate}/{toDate}")
    public ResponseEntity<MiniStatHolder> getMiniStat(@PathVariable LocalDate fromDate, @PathVariable LocalDate toDate){
        return  ResponseEntity.ok(employeeRepository.getEmployeeMiniStats(logic.dateConverter(fromDate),logic.dateConverter(toDate)));
    }

    @PostMapping("/getAllEmployeeForFeed")
    public ResponseEntity<List<EmployeeBriefDto>> getEmployeeBriefData(@RequestBody EmployeeFilterHolder filter){

        filter = providedDataChecker.defaultValueChecker(filter, EmployeeFilterHolder.class);


        return ResponseEntity.ok(
                employeeRepository.getExistingEmployeeDataForFeed(
                filter.getEmployeeAcIn(),
                filter.getEmployeeCategory(),
                filter.getEmployeeDepartment(),
                filter.getHourlyRate(),
                logic.dateConverter(filter.getFromJoined()),
                logic.dateConverter(filter.getToJoined()),
                filter.getPromt(),
                PageRequest.of(filter.getPage(), filter.getPageCount()))
        );

    }


    @PostMapping("/getTotalPages")
    public ResponseEntity<Long> getAmountOfPages(@RequestBody EmployeeFilterHolder filter) {


        filter = providedDataChecker.defaultValueChecker(filter, EmployeeFilterHolder.class);

        Long count = employeeRepository.getTotalPages(
                filter.getEmployeeAcIn(),
                filter.getEmployeeCategory(),
                filter.getEmployeeDepartment(),
                filter.getHourlyRate(),
                logic.dateConverter(filter.getFromJoined()),
                logic.dateConverter(filter.getToJoined()),
                filter.getPromt());


        return ResponseEntity.ok(count);
    }

}
