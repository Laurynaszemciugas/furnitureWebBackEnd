package com.example.jwt_demo.DTOS.Product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductPerformanceReport {

    private Long id;
    private String imageUrl;
    private String productName;
    private Long unitsSold;
    private Double revenue;
    private Double rating;

}
