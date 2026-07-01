package com.example.jwt_demo.DTOS.Employees;


import com.example.jwt_demo.Enums.EmployeeAcIn;
import com.example.jwt_demo.Enums.EmployeeCategory;
import com.example.jwt_demo.Enums.EmployeeDepartment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeBriefDto {

    private Long id;
    private String profileImage;
    private String fullName;
    private String gmail;
    private EmployeeAcIn employeeAcIn;
    private EmployeeCategory employeeCategory;
    private EmployeeDepartment employeeDepartment;
    private Double hourlyRate;
    private LocalDateTime created;

}
