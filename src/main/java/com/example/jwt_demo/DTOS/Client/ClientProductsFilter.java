package com.example.jwt_demo.DTOS.Client;

import com.example.jwt_demo.Enums.Category;
import com.example.jwt_demo.Enums.Stock;
import lombok.*;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ClientProductsFilter {

    private String prompt = "All";  // Searches names descriptions and the extra detail like width height depth
    private List<String> categories = Collections.singletonList(Category.ALL.name());
    private Double priceFrom = 100.0;
    private Double priceTo = 100000.0;
    private List<String> materials = Collections.singletonList("All");
    private String color = "All"; // searches according to color in the product
    private String stocks = Stock.ALL.toString();

}
