package com.example.jwt_demo.Common.OrderAcceptance;

import com.example.jwt_demo.Entity.Orders;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MostOrders {

    private Orders orders;
    private Double percentage;



}
