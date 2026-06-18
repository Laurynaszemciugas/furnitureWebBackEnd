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

    private Long employee;
    private Long product;
    private String promptChoice;
    private OrderStatus orderStatusChoice;
    private Double priceFromChoice;
    private Double priceToChoice;
    private LocalDate dateFromChoice;
    private LocalDate dateToChoice;
    private Long amountOfProductsChoice;
    private Long employeeId;
    private int page;
    private int pageCount;

}
