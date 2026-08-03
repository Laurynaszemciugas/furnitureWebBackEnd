package com.example.jwt_demo.DTOS.CustomReport;

import com.example.jwt_demo.Enums.DashboardWidget;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomReportFeed {

    private Long id;
    private String reportName;
    private DashboardWidget dashboardWidget;
    private String reportColor;
    private String description;
    private LocalDateTime created;

}
