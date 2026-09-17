package com.example.jwt_demo.Entity.OrderStepsJoin;

import com.example.jwt_demo.Entity.OrderJoin.OrderStepsToComplete;
import com.example.jwt_demo.Entity.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
public class OrderStepCompletionLogs {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private User employee;

    private String thingThatWasDone;


    @ManyToOne
    @JoinColumn(name = "order_steps_to_complete_id")
    @JsonBackReference("orderStepCompletionLogs")
    private OrderStepsToComplete orderStepsToComplete;




    @CreationTimestamp
    private LocalDateTime created;


}
