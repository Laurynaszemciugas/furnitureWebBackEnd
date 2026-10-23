package com.example.jwt_demo.Common.ai;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MultiProductAiDto {

    private String productName = "None";
    private String sku = "None";
    private String description = "None";
    private String productColor = "None";

    private Double price = 0.0;
    private Long discount = 0L;

    private Long lowThreshold = 0L;




    @JsonCreator
    public MultiProductAiDto() {
    }



}



