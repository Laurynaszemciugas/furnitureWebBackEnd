package com.example.jwt_demo.DTOS.Material;

import com.example.jwt_demo.Enums.ActiveInactive;
import com.example.jwt_demo.Enums.MaterialType;
import com.example.jwt_demo.Enums.Stock;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MaterialBriefDto {

    private Long id;
    private String imageUrl;
    private String name;
    private String description;
    private ActiveInactive activeInactive;
    private MaterialType materialType;
    private Stock stock;
    private Long amountLeft;
    private Long minThresh;
    private Double unitPrice;
    private LocalDateTime created;
}
