package com.example.jwt_demo.DTOS.Product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductPageMiniStat {

    private Long totalProducts;
    private Long visible;
    private Long nonVisible;
    private Long recentlyAdded;

}
