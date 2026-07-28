package com.example.jwt_demo.DTOS.Material;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MaterialInfo {

    private  Long id;
    private String imageUrl;
    private String materialName;
    private Double unitPrice;
    private Long inStock;
    private Long amountTaken;


}
