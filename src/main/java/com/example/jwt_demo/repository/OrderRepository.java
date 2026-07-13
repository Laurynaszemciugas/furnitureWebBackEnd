package com.example.jwt_demo.repository;

import com.example.jwt_demo.DTOS.Common.MiniStatHolder;
import com.example.jwt_demo.DTOS.Order.NewOrderFeedData;
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
SELECT  count(o) FROM Orders o where orderStatus = 'NEW' 

""")
    Long findNewOrdersCount();

    @Query("""
SELECT new com.example.jwt_demo.DTOS.Order.OrdersFeedData(
    o.id,
    o.orderStatus,
    COALESCE(SUM(op.amountOfProduct), 0),
    o.estimatedDueDate,
    o.created,
    o.totalPrice,
    o.billingAddress,
    o.orderCreatedByGmail
)
FROM Orders o
JOIN o.employees oe
LEFT JOIN o.productsData op
LEFT JOIN o.productsData pd
WHERE (:matId IS NULL OR pd.product.id = :matId)
AND(:empId IS NULL OR oe.employee.id = :empId)
AND (:status IS NULL OR o.orderStatus = :status)
AND (:dateFrom IS NULL OR o.created >= :dateFrom)
AND (:dateTo IS NULL OR o.estimatedDueDate <= :dateTo)
AND (:priceFrom IS NULL OR o.totalPrice >= :priceFrom)
AND (:priceTo IS NULL OR o.totalPrice <= :priceTo)
AND (
    :prompt IS NULL
    OR CAST(o.id AS string) LIKE CONCAT('%', :prompt, '%')
    OR LOWER(o.billingAddress) LIKE LOWER(CONCAT('%', :prompt, '%'))
    OR LOWER(o.phoneNumber) LIKE LOWER(CONCAT('%', :prompt, '%'))
    OR LOWER(o.orderCreatedByName) LIKE LOWER(CONCAT('%', :prompt, '%'))
    OR LOWER(o.orderCreatedByGmail) LIKE LOWER(CONCAT('%', :prompt, '%'))
)
GROUP BY o.id, o.orderStatus, o.created, o.estimatedDueDate, o.totalPrice
HAVING (:amountOfProduct IS NULL OR COALESCE(SUM(op.amountOfProduct), 0) = :amountOfProduct)
""")
    List<OrdersFeedData> getOrderData(
            @Param("status") OrderStatus status,
            @Param("priceFrom") Double priceFrom,
            @Param("priceTo") Double priceTo,
            @Param("dateFrom") LocalDateTime dateFrom,
            @Param("dateTo") LocalDateTime dateTo,
            @Param("amountOfProduct") Long amountOfProduct,
            @Param("prompt") String prompt,
            @Param("empId") Long empId,
            @Param("matId") Long matId,
            Pageable pageable
    );


    @Query("""
SELECT
    CASE
        WHEN COUNT(o.id) = 0 THEN 1
        ELSE CEIL(COUNT(o.id) / :pageCount)
    END
FROM Orders o
WHERE (:status IS NULL OR o.orderStatus = :status)
AND (:dateFrom IS NULL OR o.created >= :dateFrom)
AND (:dateTo IS NULL OR o.estimatedDueDate <= :dateTo)
AND (:priceFrom IS NULL OR o.totalPrice >= :priceFrom)
AND (:priceTo IS NULL OR o.totalPrice <= :priceTo)
AND (
      :prompt IS NULL
      OR CAST(o.id AS string) LIKE CONCAT('%', :prompt, '%')
)
AND (
      :amountOfProduct IS NULL
      OR (
            SELECT SUM(op.amountOfProduct)
            FROM o.productsData op
         ) = :amountOfProduct
)
""")
    Long getNumberOfOrderPages(
            OrderStatus status,
            Double priceFrom,
            Double priceTo,
            LocalDateTime dateFrom,
            LocalDateTime dateTo,
            Long amountOfProduct,
            String prompt,
            double pageCount
    );



    @Query("""

            SELECT new com.example.jwt_demo.DTOS.Common.MiniStatHolder(
            count(o.id),
            SUM(CASE WHEN o.orderStatus = 'Finished' THEN 1 ELSE 0 END),
            SUM(CASE WHEN o.orderStatus = 'In_Progress' THEN 1 ELSE 0 END),
            SUM(CASE WHEN o.created >= :fromDate AND o.created <= :toDate THEN 1 ELSE 0 END))
         
            FROM Orders o


""")
    MiniStatHolder getOrderMiniStats(@Param("fromDate")LocalDateTime fromDate, @Param("toDate")LocalDateTime toDate);


    @Query("""
    SELECT new com.example.jwt_demo.DTOS.Order.NewOrderFeedData(
        p.id,
        img.imageUrl,
        p.productName,
        p.sku,
        op.amountOfProduct,
        p.stockQuantity,
        p.price,
        p.price * op.amountOfProduct
    )
    FROM Orders o
    JOIN o.productsData op
    JOIN op.product p
    LEFT JOIN ProductImageData img
        ON img.product.id = p.id
        AND img.imageLogic = 'Main'
    WHERE o.id = :orderId
    """)
    List<NewOrderFeedData> getNewOrderFeedData(
            @Param("orderId") Long orderId
    );





}
