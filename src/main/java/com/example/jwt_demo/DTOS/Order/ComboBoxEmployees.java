package com.example.jwt_demo.DTOS.Order;

import com.example.jwt_demo.Enums.EmployeeRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComboBoxEmployees {


    private Long id;
    private String fullName;
    private EmployeeRole employeeCategory;
    private String profileImage;


}
