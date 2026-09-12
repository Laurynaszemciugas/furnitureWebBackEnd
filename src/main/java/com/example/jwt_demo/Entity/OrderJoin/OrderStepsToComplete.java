package com.example.jwt_demo.Entity.OrderJoin;

import com.example.jwt_demo.Entity.EmployeeJoin.OrderEmployees;
import com.example.jwt_demo.Entity.Orders;
import com.example.jwt_demo.Entity.ProductJoin.ProductFinishSteps;
import com.example.jwt_demo.Entity.User;
import com.example.jwt_demo.Enums.ProductFinishStepStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class OrderStepsToComplete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // the steps refrence removed so user could save previous steps and add new ones ofcouse new ones will now be present in already created orders

    private Long stepRealId = null;
    private Long stepId = null;
    private String stepName = null;
    private String stepDescription = null;

    private Long stepsNeeded;

    private Long stepsCompleted;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private User employee;

    @Enumerated(EnumType.STRING)
    private ProductFinishStepStatus productFinishStepStatus;

    @ManyToOne
    @JsonBackReference("orderSteps")
    private OrderProducts orderProducts;


}
