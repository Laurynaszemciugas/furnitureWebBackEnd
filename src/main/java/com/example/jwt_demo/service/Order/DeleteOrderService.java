package com.example.jwt_demo.service.Order;

import com.example.jwt_demo.Common.ErrorResponse;
import com.example.jwt_demo.Entity.Orders;
import com.example.jwt_demo.Enums.ActiveInactive;
import com.example.jwt_demo.Enums.Warnings;
import com.example.jwt_demo.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class DeleteOrderService {

    @Autowired
    OrderRepository orderRepository;

    public ResponseEntity<ErrorResponse> deleteOrderAccordingToId(Long id){

        Orders orders = orderRepository.findById(id).orElseThrow();


        try{

            orderRepository.delete(orders);

            return ResponseEntity.ok(new ErrorResponse("Deleted successfully", Warnings.OK));

        } catch (Exception e) {
            orders.setActiveInactive(ActiveInactive.INACTIVE);
            orderRepository.save(orders);
            return ResponseEntity.ok(new ErrorResponse("Order was set to Inactive",Warnings.OK));
        }

    }



}
