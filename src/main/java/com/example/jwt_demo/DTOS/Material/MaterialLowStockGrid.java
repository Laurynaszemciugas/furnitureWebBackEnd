package com.example.jwt_demo.DTOS.Material;

import com.example.jwt_demo.Enums.Stock;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MaterialLowStockGrid {

    private Long id;
    private String materialName;
    private Long inStock;
    private Long minThreshold;
    private Stock stock;

}
