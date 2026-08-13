package com.example.jwt_demo.Common.ai;

import com.example.jwt_demo.Enums.EmployeeDepartment;
import com.example.jwt_demo.Enums.EmployeeRole;
import com.example.jwt_demo.Enums.EmploymentType;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class EmployeeAiDto {



        private Double hourlySalary = null;
        private String name = null;
        private String lastName = null;
        private String emailAddress = null;

        private String phoneNumber = null;
        private LocalDate dateOfBirth = null;
        private String address = null;
        private String jobTittle = null;
        private EmploymentType employmentType = null;

        private EmployeeRole role = null;
        private EmployeeDepartment department = null;


}
