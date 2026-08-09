package com.example.jwt_demo.DTOS.DashBoard;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DashBoardMaterialStock {


    private Long lowMaterial;
    private Long noStockMaterial;

    public Boolean isEmpty() {
        return lowMaterial == 0L && noStockMaterial == 0L;
    }

}
