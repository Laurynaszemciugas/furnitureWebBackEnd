package com.example.jwt_demo.DTOS.Order;

import com.example.jwt_demo.Enums.Category;
import com.example.jwt_demo.Enums.Stock;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderAddProducts {

    private Long id;
    private String mainImage;
    private String productName;
    private String sku;
    private Category category;
    private Long stockQuantity;
    private Long lowStockThreshold;
    private Stock stock;
    private Double price;
    private Long amountSelected;

}
