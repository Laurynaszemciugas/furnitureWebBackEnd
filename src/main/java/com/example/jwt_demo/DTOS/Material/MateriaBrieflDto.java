package com.example.jwt_demo.DTOS.Material;

import com.example.jwt_demo.Enums.ActiveInactive;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MateriaBrieflDto {

    private String imageUrl;
    private String name;
    private String description;
    private ActiveInactive activeInactive;
    private LocalDateTime created;
}
