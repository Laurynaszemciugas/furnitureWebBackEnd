package com.example.jwt_demo.Entity.EmployeeJoin;


import com.example.jwt_demo.Entity.Employee;
import com.example.jwt_demo.Entity.Orders;
import com.fasterxml.jackson.annotation.JsonBackReference;
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
public class EmployeeActiveOrders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "orders_id")
    private Orders order;


    @ManyToOne
    @JoinColumn(name = "employee_id")
    @JsonBackReference("employeeActiveOrders")
    private Employee employee;


    @CreationTimestamp
    private LocalDateTime created;

}
