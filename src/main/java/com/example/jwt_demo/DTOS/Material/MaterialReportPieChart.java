package com.example.jwt_demo.DTOS.Material;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MaterialReportPieChart {


    private long inStockCount;
    private long lowStockCount;
    private long outOfStockCount;

}
