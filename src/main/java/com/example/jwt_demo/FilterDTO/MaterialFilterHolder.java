package com.example.jwt_demo.FilterDTO;

import com.example.jwt_demo.Enums.ActiveInactive;
import com.example.jwt_demo.Enums.MaterialType;
import com.example.jwt_demo.Enums.Stock;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MaterialFilterHolder {


    private MaterialType materialTypeChoice;
    private ActiveInactive activeInactive;
    private Long stockAmountChoice;
    private Long minThresholdChoice;
    private Double unitPriceChoice;
    private LocalDate fromDateChoice;
    private LocalDate todDateChoice;
    private Stock stockChoice;
    private String promtChoice;
    private int page;
    private int pageCount;


}
