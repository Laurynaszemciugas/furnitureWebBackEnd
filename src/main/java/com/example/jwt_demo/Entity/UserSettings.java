package com.example.jwt_demo.Entity;

import com.example.jwt_demo.Enums.DateFormat;
import com.example.jwt_demo.Enums.Language;
import com.example.jwt_demo.Enums.TimeZone;
import com.example.jwt_demo.Enums.Verification;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UserSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DateFormat dateFormat = DateFormat.DD_MM_YYYY;

    @Enumerated(EnumType.STRING)
    private TimeZone timeZone = TimeZone.UTC;

    @Enumerated(EnumType.STRING)
    private Language language = Language.EN;

    private boolean receiveGmail = true;

    private String theme = "Light";

    private String accent = "Blue";

    private String sidebarSize = "Large";

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;


}
