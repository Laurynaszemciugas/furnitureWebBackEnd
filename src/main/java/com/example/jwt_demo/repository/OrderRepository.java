package com.example.jwt_demo.repository;

import com.example.jwt_demo.Entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Orders,Long> {


    @Query("""
SELECT DISTINCT o FROM Orders o
LEFT JOIN FETCH o.productsData op
LEFT JOIN FETCH op.product
LEFT JOIN FETCH o.user
""")
    List<Orders> findAllFull();


}
