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


    private long lowMaterial;
    private long noStockMaterial;

    public boolean isEmpty() {
        return lowMaterial == 0L && noStockMaterial == 0L;
    }

}
