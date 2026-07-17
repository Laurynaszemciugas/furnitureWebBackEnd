package com.example.jwt_demo.DTOS.Order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderReportPieChart {


    private long newCount;
    private long lackOfSupplyCount;
    private long pendingCount;
    private long inProgressCount;
    private long finishedCount;
    private long cancelledCount;

}
