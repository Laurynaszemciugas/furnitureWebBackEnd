package com.example.jwt_demo.repository;

import com.example.jwt_demo.DTOS.Order.OrdersFeedData;
import com.example.jwt_demo.Entity.Orders;
import com.example.jwt_demo.Enums.OrderStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Orders,Long> {


    @Query("""
SELECT DISTINCT o FROM Orders o
LEFT JOIN FETCH o.productsData op
LEFT JOIN FETCH op.product
LEFT JOIN FETCH o.user
""")
    List<Orders> findAllFull();

    @Query("""
SELECT new com.example.jwt_demo.DTOS.Order.OrdersFeedData(
    o.id,
    o.orderStatus,
    COUNT(op.id),
    o.created,
    o.estimatedDueDate
)
FROM Orders o
LEFT JOIN o.productsData op
WHERE (:status IS NULL OR o.orderStatus = :status)
AND (:dateFrom IS NULL OR o.created >= :dateFrom)
AND (:dateTo IS NULL OR o.estimatedDueDate <= :dateTo)
AND (:priceFrom IS NULL OR o.totalPrice >= :priceFrom)
AND (:priceTo IS NULL OR o.totalPrice <= :priceTo)
GROUP BY o.id, o.orderStatus, o.created, o.estimatedDueDate
HAVING (:amountOfProduct IS NULL OR COUNT(op.id) = :amountOfProduct)
""")
    List<OrdersFeedData> getOrderData(
            @Param("status") OrderStatus status,
            @Param("priceFrom") Double priceFrom,
            @Param("priceTo") Double priceTo,
            @Param("dateFrom") LocalDateTime dateFrom,
            @Param("dateTo") LocalDateTime dateTo,
            @Param("amountOfProduct") Long amountOfProduct,
            Pageable pageable
    );





}
