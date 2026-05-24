package com.example.jwt_demo.Entity.EmployeeJoin;

import com.example.jwt_demo.Entity.Employee;
import com.example.jwt_demo.Entity.Orders;
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
public class OrderEmployees {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Orders order;

    @CreationTimestamp
    private LocalDateTime created;

}
