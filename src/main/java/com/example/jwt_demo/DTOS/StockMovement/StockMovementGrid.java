package com.example.jwt_demo.DTOS.StockMovement;

import com.example.jwt_demo.Enums.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockMovementGrid {

    private LocalDateTime created;
    private String materialName;
    private Type type;
    private Long qty;
    private Long balance;

}
