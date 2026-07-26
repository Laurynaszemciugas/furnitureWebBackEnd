package com.example.jwt_demo.DTOS.Product;

import com.example.jwt_demo.Enums.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductReportPieChart {

    private Category category;
    private Long value;

}
