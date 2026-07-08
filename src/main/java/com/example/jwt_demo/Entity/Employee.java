package com.example.jwt_demo.Entity;

import com.example.jwt_demo.Enums.EmployeeAcIn;
import com.example.jwt_demo.Enums.EmployeeDepartment;
import com.example.jwt_demo.Enums.EmployeeRole;
import com.example.jwt_demo.Enums.EmploymentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double hourlyRate;
    private Long productsFinished;
    private String name;
    private String lastName;
    private String fullName;
    private String gmail;



    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String address;
    private String jobTittle;
    @Enumerated(EnumType.STRING)
    private EmploymentType employmentType;

    @Lob
    private String profileImage;


    @Enumerated(EnumType.STRING)
    private EmployeeAcIn employeeAcIn;
    @Enumerated(EnumType.STRING)
    private EmployeeRole employeeCategory;
    @Enumerated(EnumType.STRING)
    private EmployeeDepartment employeeDepartment;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private User empId;

    @CreationTimestamp
    private LocalDateTime created;


}
