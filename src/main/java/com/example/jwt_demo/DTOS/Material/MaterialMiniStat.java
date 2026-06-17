package com.example.jwt_demo.DTOS.Material;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MaterialMiniStat {

    private Long totalMaterials;
    private Long activeMaterials;
    private Long inactiveMaterials;
    private Long recentlyAdded;

}
