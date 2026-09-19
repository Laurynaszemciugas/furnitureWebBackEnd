package com.example.jwt_demo.DTOS.EmployeePage;

import com.example.jwt_demo.Enums.OrderStatus;
import com.example.jwt_demo.Enums.Priority;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeOrderProjection {

    private Long id;
    private LocalDateTime created;
    private LocalDateTime dueDate;
    private OrderStatus orderStatus;
    private Long amountOfItems;

    private Long estimatedFinishTimeMinutes;
    private Priority priority;

    private Object images;

    private Object employeeNames;

    private Object employeeImages;



}
