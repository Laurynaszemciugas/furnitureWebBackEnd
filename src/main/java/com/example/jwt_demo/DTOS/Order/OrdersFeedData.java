package com.example.jwt_demo.DTOS.Order;

import com.example.jwt_demo.Enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrdersFeedData {

    private Long id;
    private OrderStatus orderStatus;
    private Long productCount;
    private LocalDateTime estimatedDueDate;
    private LocalDateTime created;
    private Double totalPrice;
    private String address;
    private String gmail;
}
