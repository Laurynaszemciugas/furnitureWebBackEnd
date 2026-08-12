package com.example.jwt_demo.Common.ai;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AiQuestion <T> {

    private String prompt;
    private String referenceToDataNeeded;

}
