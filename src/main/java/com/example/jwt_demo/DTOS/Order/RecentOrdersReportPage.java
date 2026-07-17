package com.example.jwt_demo.DTOS.Order;

import com.example.jwt_demo.Enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecentOrdersReportPage {

    private Long id;
    private Long productCount;
    private OrderStatus orderStatus;
    private Double value;
    private LocalDateTime dueDate;

}
