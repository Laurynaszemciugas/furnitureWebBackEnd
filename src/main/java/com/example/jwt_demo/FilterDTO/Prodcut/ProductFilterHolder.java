package com.example.jwt_demo.FilterDTO.Prodcut;

import com.example.jwt_demo.Enums.Category;
import com.example.jwt_demo.Enums.Stock;
import com.example.jwt_demo.Enums.Visibility;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductFilterHolder {

    private String prompt;
    private Stock stockChoice;
    private Category category;
    private Visibility visibility;
    private LocalDate createdFrom;
    private LocalDate createdTo;
    private Long discount;
    private Double price;
    private Long materialId;
    private int page;
    private int pageCount;




}
