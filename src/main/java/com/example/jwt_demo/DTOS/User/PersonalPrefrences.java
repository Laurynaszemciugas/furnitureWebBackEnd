package com.example.jwt_demo.DTOS.User;

import com.example.jwt_demo.Enums.DateFormat;
import com.example.jwt_demo.Enums.Language;
import com.example.jwt_demo.Enums.TimeZone;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PersonalPrefrences {



    private DateFormat dateFormat;
    private TimeZone timeZone;
    private Language language;
    private boolean activeNotification;


}
