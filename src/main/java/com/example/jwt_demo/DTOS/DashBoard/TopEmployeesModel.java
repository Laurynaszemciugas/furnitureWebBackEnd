package com.example.jwt_demo.DTOS.DashBoard;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TopEmployeesModel {

    private String profilePicUrl;
    private String name;
    private Long unitsProduced;
    private Double hourlySalary;
    private Long hoursWorkedTotal;
    private Long hoursWorkedThisMonth;


}
