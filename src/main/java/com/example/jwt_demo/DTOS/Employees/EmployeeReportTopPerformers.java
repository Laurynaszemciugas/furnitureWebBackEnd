package com.example.jwt_demo.DTOS.Employees;

import com.example.jwt_demo.Enums.EmployeeDepartment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeReportTopPerformers {


    private Long id;
    private String imageUrl;
    private String fullName;
    private EmployeeDepartment employeeDepartment;
    private Long finishedOrders;
    private Double hoursWorked;


}
