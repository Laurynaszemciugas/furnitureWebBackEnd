package com.example.jwt_demo.controller;

import com.example.jwt_demo.Common.ErrorResponse;
import com.example.jwt_demo.Common.Logic;
import com.example.jwt_demo.Common.ProvidedDataChecker;
import com.example.jwt_demo.DTOS.Common.MiniStatHolder;
import com.example.jwt_demo.DTOS.Employees.EmployeeBriefDto;
import com.example.jwt_demo.DTOS.Order.ComboBoxEmployees;
import com.example.jwt_demo.Entity.Employee;
import com.example.jwt_demo.Entity.Materials;
import com.example.jwt_demo.Entity.User;
import com.example.jwt_demo.Enums.ActiveInactive;
import com.example.jwt_demo.Enums.EmployeeAcIn;
import com.example.jwt_demo.Enums.Role;
import com.example.jwt_demo.Enums.Warnings;
import com.example.jwt_demo.FilterDTO.Employee.EmployeeFilterHolder;
import com.example.jwt_demo.FilterDTO.Material.MaterialFilterHolder;
import com.example.jwt_demo.repository.EmployeeRepository;
import com.example.jwt_demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProvidedDataChecker providedDataChecker;

    @Autowired
    AuthController authController;

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

    @PostMapping("/saveNewEmployee")
    public ResponseEntity<ErrorResponse> saveNewEmploee(@RequestBody Employee emp){


        User empUser = new User();
        empUser.setGmail(emp.getGmail());
        empUser.setName(emp.getName());
        empUser.setLastName(emp.getLastName());
        empUser.setFullName(emp.getName() + " " + emp.getLastName());
        empUser.setPassword(emp.getUser().getPassword());
        empUser.setRole(Role.EMPLOYEE);
        empUser.setCreated(LocalDateTime.now());
        authController.systemRegister(empUser);

        User savedEmpUser = userRepository.findByGmail(emp.getGmail());

        Employee cleanEmpLoyee = new Employee();
        cleanEmpLoyee.setHourlyRate(emp.getHourlyRate());
        cleanEmpLoyee.setProductsFinished(0L); // count somehow btw
        cleanEmpLoyee.setName(emp.getName());
        cleanEmpLoyee.setLastName(emp.getLastName());
        cleanEmpLoyee.setFullName(emp.getName() + " " + emp.getLastName());
        cleanEmpLoyee.setGmail(emp.getGmail());
        cleanEmpLoyee.setPhoneNumber(emp.getPhoneNumber());
        cleanEmpLoyee.setDateOfBirth(emp.getDateOfBirth());
        cleanEmpLoyee.setAddress(emp.getAddress());
        cleanEmpLoyee.setJobTittle(emp.getJobTittle());
        cleanEmpLoyee.setEmploymentType(emp.getEmploymentType());
        cleanEmpLoyee.setProfileImage(emp.getProfileImage());
        cleanEmpLoyee.setEmployeeAcIn(emp.getEmployeeAcIn());
        cleanEmpLoyee.setEmployeeCategory(emp.getEmployeeCategory());
        cleanEmpLoyee.setEmployeeDepartment(emp.getEmployeeDepartment());
        //cleanEmpLoyee.setUser();
        cleanEmpLoyee.setCreated(LocalDateTime.now());


        cleanEmpLoyee.setEmpId(savedEmpUser);


        employeeRepository.save(cleanEmpLoyee);


        return ResponseEntity.ok(new ErrorResponse("saved", Warnings.OK));


    }

    @PostMapping("/editEmployee")
    public ResponseEntity<ErrorResponse> editEmployee(@RequestBody Employee emp){



        Employee cleanEmpLoyee = employeeRepository.findById(emp.getId()).orElseThrow();
        cleanEmpLoyee.setHourlyRate(emp.getHourlyRate());
        cleanEmpLoyee.setProductsFinished(0L); // count somehow btw
        cleanEmpLoyee.setName(emp.getName());
        cleanEmpLoyee.setLastName(emp.getLastName());
        cleanEmpLoyee.setFullName(emp.getName() + " " + emp.getLastName());
        cleanEmpLoyee.setGmail(emp.getGmail());
        cleanEmpLoyee.setPhoneNumber(emp.getPhoneNumber());
        cleanEmpLoyee.setDateOfBirth(emp.getDateOfBirth());
        cleanEmpLoyee.setAddress(emp.getAddress());
        cleanEmpLoyee.setJobTittle(emp.getJobTittle());
        cleanEmpLoyee.setEmploymentType(emp.getEmploymentType());
        cleanEmpLoyee.setProfileImage(emp.getProfileImage());
        cleanEmpLoyee.setEmployeeAcIn(emp.getEmployeeAcIn());
        cleanEmpLoyee.setEmployeeCategory(emp.getEmployeeCategory());
        cleanEmpLoyee.setEmployeeDepartment(emp.getEmployeeDepartment());
        //cleanEmpLoyee.setUser();
        cleanEmpLoyee.setCreated(LocalDateTime.now());


        User empUser = cleanEmpLoyee.getEmpId();
        empUser.setGmail(emp.getGmail());
        empUser.setName(emp.getName());
        empUser.setLastName(emp.getLastName());
        empUser.setFullName(emp.getName() + " " + emp.getLastName());

        if(emp.getUser().getPassword().length() >= 8 || !emp.getUser().getPassword().isEmpty() ) {
            empUser.setPassword(logic.passwordEncoder().encode(emp.getUser().getPassword()));
        }

        employeeRepository.save(cleanEmpLoyee);


        employeeRepository.save(cleanEmpLoyee);

        return ResponseEntity.ok(new ErrorResponse("Edited", Warnings.OK));


    }


    @GetMapping("/deleteEmployee/{id}")
    public ResponseEntity<ErrorResponse> deleteEmployee(@PathVariable Long id){

        Employee employee = employeeRepository.findById(id).orElseThrow();

        try{

            employeeRepository.deleteById(id);

        }catch (Exception e){


            employee.setEmployeeAcIn(EmployeeAcIn.INACTIVE);

            employeeRepository.save(employee);

            return ResponseEntity.ok(new ErrorResponse(  employee.getFullName() + " was set to Inactive successfully", Warnings.OK));

        }
        return ResponseEntity.ok(new ErrorResponse(employee.getFullName() + "Material removed successfully", Warnings.OK));
    }

    @GetMapping("/getEmployee/{id}")
    public ResponseEntity<Employee> getEditData(@PathVariable Long id){

        Employee employee = employeeRepository.findById(id).orElseThrow();


        return ResponseEntity.ok(employee);
    }



}
