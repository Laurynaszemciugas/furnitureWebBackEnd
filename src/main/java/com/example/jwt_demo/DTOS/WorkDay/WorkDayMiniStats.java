package com.example.jwt_demo.DTOS.WorkDay;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkDayMiniStats {

    private Long totalMinutes;
    private Long workDay;
    private Long totalWorkDone;
    private Double averageDay;

}
