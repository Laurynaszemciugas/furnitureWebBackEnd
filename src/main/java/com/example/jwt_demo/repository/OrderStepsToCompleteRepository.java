package com.example.jwt_demo.repository;

import com.example.jwt_demo.Entity.OrderJoin.OrderStepsToComplete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface OrderStepsToCompleteRepository extends JpaRepository<OrderStepsToComplete,Long> {


    @Query("""
    Select o.id
    From OrderStepsToComplete o
    where o.orderProducts.id = :id and o.uuId = :uuid
""")

    Long getOrderStepsToComplete(Long id, String uuid);



}
