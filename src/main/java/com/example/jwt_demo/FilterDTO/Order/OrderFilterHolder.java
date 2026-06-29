package com.example.jwt_demo.FilterDTO.Order;

import com.example.jwt_demo.Enums.OrderStatus;
import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderFilterHolder {

    private Long employee = 0L;
    private Long products = 0L;
    private String promptChoice = "ALL";
    private OrderStatus orderStatusChoice = OrderStatus.ALL;
    private Double priceFromChoice = 0.0;
    private Double priceToChoice = 0.0;
    private LocalDate dateFromChoice= LocalDate.of(1000,12,12);
    private LocalDate dateToChoice= LocalDate.of(1000,12,12);
    private Long amountOfProductsChoice= 0L;
    private int page;
    private int pageCount;

}
