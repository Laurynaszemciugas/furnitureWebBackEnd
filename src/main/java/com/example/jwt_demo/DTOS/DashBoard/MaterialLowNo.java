package com.example.jwt_demo.DTOS.DashBoard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MaterialLowNo {

    private long id;
    private String name;
    private long currentStock;
    private long minTreshold;
    private double pieceCost;

}
