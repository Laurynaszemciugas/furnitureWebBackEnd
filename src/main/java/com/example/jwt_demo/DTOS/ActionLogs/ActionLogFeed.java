package com.example.jwt_demo.DTOS.ActionLogs;

import com.example.jwt_demo.Entity.User;
import com.example.jwt_demo.Enums.ActionDesciptionEnum;
import com.example.jwt_demo.Enums.ActionTrackerEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActionLogFeed {


    private String actionName;
    private User whoMadeIt;
    private ActionTrackerEnum typeOfActionRecorded;
    private ActionDesciptionEnum action;
    private LocalDateTime created;


}
