package com.example.jwt_demo.Common.ai;

import com.example.jwt_demo.Entity.ExtraDetails;
import com.example.jwt_demo.Entity.ProductJoin.ProductFinishSteps;
import com.example.jwt_demo.Enums.Category;
import lombok.*;

import java.util.List;
import java.util.function.Consumer;

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
    private List<ProductFinishSteps> productFinishStepsList = null;

    private List<ExtraDetails> extraDetails = null;


}



