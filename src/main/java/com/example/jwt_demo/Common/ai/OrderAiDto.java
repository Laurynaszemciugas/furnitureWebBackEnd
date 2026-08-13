package com.example.jwt_demo.Common.ai;

import com.example.jwt_demo.Enums.PayMethod;
import com.example.jwt_demo.Enums.PayStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderAiDto {



    private String orderNote = null;
    private LocalDateTime estimatedDueDate = null;
    private String phoneNumber = null;
    private String billingAddress = null;
    private String orderCreatedByName = null;
    private String orderCreatedByGmail = null;



    }
