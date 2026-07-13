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

public class NewOrderFeedData {

    private Long id;
    private String mainImage;
    private String name;
    private String sku;
    private Long quantity;
    private Long remainingAmount;
    private Double unitPrice;
    private Double total;


}

