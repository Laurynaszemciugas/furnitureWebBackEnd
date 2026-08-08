package com.example.jwt_demo.DTOS.DashBoard;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DashBoardMaterialUsageInfo {


    private String mostUsedMaterial;
    private long totalMaterialsUsed;
    private double totalUsedMaterialCost;
    private double lastMonthTotalUsedMaterialCost;

    public boolean isEmpty() {
        return mostUsedMaterial == null
                && totalMaterialsUsed == 0
                && totalUsedMaterialCost == 0.0
                && lastMonthTotalUsedMaterialCost == 0.0;
    }

}
