package com.example.jwt_demo.DTOS.Common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GraphDataDateValue {

    private LocalDate localDate;
    private double value;


}
