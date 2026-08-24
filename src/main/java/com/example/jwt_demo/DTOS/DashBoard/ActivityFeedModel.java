package com.example.jwt_demo.DTOS.DashBoard;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActivityFeedModel {

    private String actionDescription;
    private Long howLongAgoMinutes;
    private String name;

}
