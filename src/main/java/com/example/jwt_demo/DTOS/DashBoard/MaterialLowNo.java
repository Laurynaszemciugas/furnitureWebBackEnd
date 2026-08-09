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

    private Long id;
    private String name;
    private Long currentStock;
    private Long minTreshold;
    private Double pieceCost;

}
