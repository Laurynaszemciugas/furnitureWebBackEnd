package com.example.jwt_demo.Entity;

import com.example.jwt_demo.Enums.EmployeeCategory;
import com.example.jwt_demo.Enums.EnabledDisabled;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

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
    private String profileImage;

    private String password;

    @Enumerated(EnumType.STRING)
    private EnabledDisabled enabledDisabled;
    @Enumerated(EnumType.STRING)
    private EmployeeCategory employeeCategory;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @CreationTimestamp
    private LocalDateTime created;


}
