package com.example.jwt_demo.DTOS.DashBoard;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DashBoardMonthlyOrdersCompleted {

    private Long thisMonthOrders;
    private Long previousMonthOrders;

    public boolean isEmpty() {
        return thisMonthOrders == 0 && previousMonthOrders == 0;
    }

}
