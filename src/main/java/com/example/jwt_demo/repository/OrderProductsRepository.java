package com.example.jwt_demo.repository;

import com.example.jwt_demo.Entity.OrderJoin.OrderProducts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductsRepository extends JpaRepository<OrderProducts, Long> {
}
