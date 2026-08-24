package com.example.jwt_demo.Common.ai;

import com.example.jwt_demo.Enums.Category;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductAiDto {

    private String productName = null;
    private String sku = null;
    private String description = null;

    private Double price = null;
    private Long discount = null;

    private Long stockQuantity = null;
    private Long lowThreshold = null;
    private Category category = null;


}



