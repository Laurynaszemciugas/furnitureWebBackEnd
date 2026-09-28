package com.example.jwt_demo.FilterDTO.Material;

import com.example.jwt_demo.Enums.Category;
import com.example.jwt_demo.Enums.ProductCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MaterialViewOnProductUsageFilter {


    private Long id;
    private String prompt = "ALL";
    private Category productCategory = Category.ALL;

    private int page;
    private int pageCount;


}
