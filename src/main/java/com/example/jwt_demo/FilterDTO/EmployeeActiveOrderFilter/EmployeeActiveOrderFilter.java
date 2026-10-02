package com.example.jwt_demo.FilterDTO.EmployeeActiveOrderFilter;

import com.example.jwt_demo.Enums.OrderStatus;
import com.example.jwt_demo.Enums.Priority;
import com.example.jwt_demo.Enums.SortOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeActiveOrderFilter {

    private String promt = "ALL";
    private OrderStatus orderStatus = OrderStatus.ALL;
    private Priority priority = Priority.ALL;
    private SortOrder sortOrder = SortOrder.ALL;


    private Long empId = -1L;


    private int page = 0;
    private int pageCount = 5;
}
