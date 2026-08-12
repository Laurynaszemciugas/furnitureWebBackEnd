package com.example.jwt_demo.Common.ai;

import com.example.jwt_demo.Enums.ActiveInactive;
import com.example.jwt_demo.Enums.MaterialGrainPatterns;
import com.example.jwt_demo.Enums.MaterialTextures;
import com.example.jwt_demo.Enums.MaterialType;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MaterialAiDto {

    private String materialName = null;
    private MaterialType materialType = null;
    private String materialUrl = null;

    private String description = null;
    private String careInstructions = null;

    private String materialColor = null;
    private MaterialType materialFinishType = null;
    private MaterialTextures materialTexture = null;
    private MaterialGrainPatterns materialGrainPattern = null;

    private Double materialPrice = null;
    private Double materialUnitWeight = null;
    private Long materialMinThreshold = null;
    private Long materialStock = null;
    private String materialUnit = null;

    private LocalDate deliveryDate = null;
    private Long defaultRestockPeriod = null;


}



