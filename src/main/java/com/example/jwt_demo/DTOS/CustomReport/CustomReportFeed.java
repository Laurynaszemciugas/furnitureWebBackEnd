package com.example.jwt_demo.DTOS.CustomReport;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomReportFeed {

    private Long id;
    private String reportName;
    private String reportColor;

}
