package com.example.jwt_demo.service.Order;

import com.example.jwt_demo.Common.ActionMaker;
import com.example.jwt_demo.Common.ErrorResponse;
import com.example.jwt_demo.Entity.Orders;
import com.example.jwt_demo.Enums.ActionDesciptionEnum;
import com.example.jwt_demo.Enums.ActionTrackerEnum;
import com.example.jwt_demo.Enums.OrderStatus;
import com.example.jwt_demo.Enums.Warnings;
import com.example.jwt_demo.controller.Common;
import com.example.jwt_demo.repository.OrderRepository;
import com.example.jwt_demo.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ModifyDataOrdersService {


    @Autowired
    Common common;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ActionMaker actionMaker;

    public ResponseEntity<ErrorResponse> rejectNewOrder(Long id, CustomUserDetails user){


        Orders newOrder = orderRepository.findById(id).orElseThrow();
        newOrder.setOrderStatus(OrderStatus.CANCELLED);

        orderRepository.save(newOrder);

        actionMaker.makeAction(String.format("Order [ORD-%d] was rejected successfully",newOrder.getId()),user.getId(),null, ActionTrackerEnum.USER, ActionDesciptionEnum.Order_Status_Change);



        return ResponseEntity.ok(new ErrorResponse("Changed successfully to cancelled", Warnings.OK));

    }



    public ResponseEntity<ErrorResponse> acceptNewOrder( Long id, CustomUserDetails user){

        Orders newOrder = orderRepository.findById(id).orElseThrow();


        if(newOrder.getOrderStatus().equals(OrderStatus.AWAITING_CONFIRMATION)){
            newOrder.setOrderStatus(OrderStatus.Pending);
            orderRepository.save(newOrder);

            actionMaker.makeAction(String.format("Order [ORD-%d] Changed successfully to Pending",newOrder.getId()),user.getId(),null,ActionTrackerEnum.USER, ActionDesciptionEnum.Order_Status_Change);
        }

        else{
            return ResponseEntity.ok(new ErrorResponse("Order cannot be accepted due to lack of supply", Warnings.ERROR));
        }





        return ResponseEntity.ok(new ErrorResponse("Changed successfully to Pending", Warnings.OK));

    }


}
