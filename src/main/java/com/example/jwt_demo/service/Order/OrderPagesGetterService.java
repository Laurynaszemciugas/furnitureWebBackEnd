package com.example.jwt_demo.service.Order;

import com.example.jwt_demo.Common.Logic;
import com.example.jwt_demo.Common.ProvidedDataChecker;
import com.example.jwt_demo.FilterDTO.Order.OrderFilterHolder;
import com.example.jwt_demo.repository.OrderRepository;
import com.example.jwt_demo.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class OrderPagesGetterService {


    @Autowired
    ProvidedDataChecker providedDataChecker;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    Logic logic;


    // Order page main page count
    public ResponseEntity<Long> orderPagePageCount(@RequestBody OrderFilterHolder orderFilterHolder, CustomUserDetails user) {

        orderFilterHolder = providedDataChecker.defaultValueChecker(orderFilterHolder, OrderFilterHolder.class);

        Long count = orderRepository.getNumberOfOrderPages(
                orderFilterHolder.getOrderStatusChoice(),
                orderFilterHolder.getPriceFromChoice(),
                orderFilterHolder.getPriceToChoice(),
                logic.dateConverter(orderFilterHolder.getDateFromChoice()),
                logic.dateConverter(orderFilterHolder.getDateToChoice()),
                orderFilterHolder.getPromptChoice(),
                orderFilterHolder.getEmployee(),
                orderFilterHolder.getProducts(),
                orderFilterHolder.getOrderActiveInactive(),
                orderFilterHolder.getPageCount(),
                user.getId()

        );

        return ResponseEntity.ok(
                count
        );
    }


    // order page new order pages
    public ResponseEntity<Long> newOrderPageCount(CustomUserDetails user){

        Long count = orderRepository.getNewOrderTotalPages(
                user.getId()

        );

        return ResponseEntity.ok(
                count
        );

    }



    public ResponseEntity<Long> getNewOrderCount(CustomUserDetails user){

        return ResponseEntity.ok(orderRepository.findNewOrdersCount(user.getId()));

    }





}
