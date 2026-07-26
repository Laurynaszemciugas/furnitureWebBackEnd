package com.example.jwt_demo.DTOS.Product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductLowStockList {

    private Long id;
    private String imageUrl;
    private String productName;
    private Long stockLeft;
    private Long lowThreshold;
    private boolean userDriven;

}
