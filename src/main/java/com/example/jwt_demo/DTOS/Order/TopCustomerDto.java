package com.example.jwt_demo.DTOS.Order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TopCustomerDto {

    private Long id;
    private String name;
    private Long orders;
    private Double revenue;
    private Double averageRevenue;


}
