package com.example.jwt_demo.Entity;

import com.example.jwt_demo.Entity.OrderJoin.OrderStepsToComplete;
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
public class WorkDone {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "work_day_id")
    @JsonBackReference(value = "workDone")
    private WorkDay workDay;


    @ManyToOne
    @JoinColumn(name = "employee_id")
    @JsonBackReference(value = "workDoneEmp")
    private Employee employee;

    @CreationTimestamp
    private LocalDateTime started;


    @ManyToOne
    @JoinColumn(name = "order_steps_to_complete_id")
    private OrderStepsToComplete orderStepsToComplete;


    @ManyToOne
    @JoinColumn(name = "order_id")

    private Orders order;


    private String whatWasDone;

    private String employeeNote;




}
