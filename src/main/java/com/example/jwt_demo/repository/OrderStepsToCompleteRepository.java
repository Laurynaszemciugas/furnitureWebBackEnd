package com.example.jwt_demo.repository;

import com.example.jwt_demo.Entity.OrderJoin.OrderStepsToComplete;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OrderStepsToCompleteRepository extends JpaRepository<OrderStepsToComplete,Long> {



}
