package com.example.jwt_demo.FrontEndModels;

import com.example.jwt_demo.Enums.Category;
import com.example.jwt_demo.Enums.ProductCategory;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductFeedModel {

    private Long id;
    private String imageUrl;
    private String productName;
    private Category category;
    private double price;
    private Long stockQuantity;
    private Long lowStockThreshold;
}
