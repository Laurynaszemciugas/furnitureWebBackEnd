package com.example.jwt_demo.FilterDTO.ActionLog;


import com.example.jwt_demo.Enums.ActionDesciptionEnum;
import com.example.jwt_demo.Enums.ActionTrackerEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActionLogFilterHolder {

    private ActionTrackerEnum whoMadeTheAction = ActionTrackerEnum.ALL;
    private String promt = "ALL";

    private ActionDesciptionEnum actionType = ActionDesciptionEnum.ALL;
    private LocalDate dateFrom = LocalDate.of(1000,12,12);
    private LocalDate dateTo = LocalDate.of(1000,12,12);


    private int page = 0;
    private int pageCount = 5;

}
