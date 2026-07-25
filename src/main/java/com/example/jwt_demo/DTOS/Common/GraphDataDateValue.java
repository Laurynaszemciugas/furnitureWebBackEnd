package com.example.jwt_demo.DTOS.Common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class GraphDataDateValue {

    private LocalDate localDate;
    private Double value;

    public GraphDataDateValue(LocalDate localDate, Double value) {
        this.localDate = localDate;
        this.value = value;
    }

    public GraphDataDateValue(Date date, Double value) {
        this.localDate = date.toLocalDate();
        this.value = value;
    }

}
