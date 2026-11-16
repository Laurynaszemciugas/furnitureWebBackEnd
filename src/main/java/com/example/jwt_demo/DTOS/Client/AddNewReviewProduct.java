package com.example.jwt_demo.DTOS.Client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddNewReviewProduct {

    private String comment;
    private Long review;
    private Long productId;

}
