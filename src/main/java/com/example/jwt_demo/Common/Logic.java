package com.example.jwt_demo.Common;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class Logic {

    public LocalDateTime dateConverter(LocalDate givenDate){

        if(!givenDate.equals(LocalDate.of(1000,12,12))) {
            String fromInput = String.format("%s %s", givenDate, "13:42:46.614631");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

            return LocalDateTime.parse(fromInput, formatter);
        }

        else{
            return null;
        }
    }


}
