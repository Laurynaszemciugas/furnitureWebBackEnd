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
    private long unitsProduced;
    private double hourlySalary;
    private long hoursWorkedTotal;
    private long hoursWorkedThisMonth;


}
