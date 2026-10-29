package com.example.jwt_demo.DTOS.Client;

import com.example.jwt_demo.Enums.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductsClients {


    private Long id;
    private String productName;
    private Category category;
    private boolean multiVariants;
    private String nonMultiVariantImage;
    private Object multiVariantImages;
    private Object multiVariantPrices;
    private Object multiVariantDiscounts;
    private Double normalProductPrice;
    private Long normalProductDiscount;

}
