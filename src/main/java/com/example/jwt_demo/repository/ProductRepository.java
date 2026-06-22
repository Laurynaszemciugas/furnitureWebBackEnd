package com.example.jwt_demo.repository;

import com.example.jwt_demo.DTOS.Common.MiniStatHolder;
import com.example.jwt_demo.DTOS.Material.MaterialBriefDto;
import com.example.jwt_demo.DTOS.Material.MaterialMiniStat;
import com.example.jwt_demo.DTOS.Order.OrderAddProducts;
import com.example.jwt_demo.DTOS.Product.ProductPageMiniStat;
import com.example.jwt_demo.Entity.Product;
import com.example.jwt_demo.Enums.Category;
import com.example.jwt_demo.Enums.Stock;
import com.example.jwt_demo.Enums.Visibility;
import com.example.jwt_demo.DTOS.Product.ProductFeedModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Long> {

    Optional<Product> findById(Long id);

    @Query("""
SELECT new com.example.jwt_demo.DTOS.Product.ProductFeedModel(
    p.id,
    i.imageUrl,
    p.productName,
    p.category,
    p.price,
    p.stockQuantity,
    p.lowStockThreshold,
    p.discount,
    p.price - (p.price * p.discount / 100.0),
    p.visibility
)
FROM Product p
LEFT JOIN ProductImageData i 
    ON i.product.id = p.id 
    AND i.imageLogic = 'Main'
WHERE p.user.id = :userId

AND (:category IS NULL OR p.category = :category)
AND (:stock IS NULL OR p.stock = :stock)
AND (:visibility IS NULL OR p.visibility = :visibility)

AND (:prompt IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :prompt, '%')))

AND (:createdFrom IS NULL OR p.created >= :createdFrom)
AND (:createdTo IS NULL OR p.created <= :createdTo)

AND (:price IS NULL OR p.price <= :price)
AND (:discount IS NULL OR p.discount >= :discount)

AND (
    :materialId IS NULL
    OR EXISTS (
        SELECT 1
        FROM ProductMaterials pm
        WHERE pm.product.id = p.id
        AND pm.materials.id = :materialId
    )
)
""")
    List<ProductFeedModel> getAllProducts(
            @Param("category") Category category,
            @Param("stock") Stock stock,
            @Param("visibility") Visibility visibility,
            @Param("prompt") String prompt,
            @Param("createdFrom") LocalDateTime createdFrom,
            @Param("createdTo") LocalDateTime createdTo,
            @Param("price") Double price,
            @Param("discount") Long discount,
            @Param("materialId") Long materialId,
            @Param("userId") Long userId,
            Pageable pageable
    );


    @Query("""

    SELECT CASE  WHEN COUNT(p.id) = 0 THEN 1 ELSE CEIL(COUNT(p.id) / 20.0) END
    FROM Product p
    
    
WHERE p.user.id = :userId

    
AND (:category IS NULL OR p.category = :category)
AND (:stock IS NULL OR p.stock = :stock)
AND (:visibility IS NULL OR p.visibility = :visibility)

AND (:prompt IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :prompt, '%')))

AND (:createdFrom IS NULL OR p.created >= :createdFrom)
AND (:createdTo IS NULL OR p.created <= :createdTo)

AND (:price IS NULL OR p.price <= :price)
AND (:discount IS NULL OR p.discount >= :discount)

AND (
    :materialId IS NULL
    OR EXISTS (
        SELECT 1
        FROM ProductMaterials pm
        WHERE pm.product.id = p.id
        AND pm.materials.id = :materialId
    )
    
    )
    
""")
    Long getProductPages(@Param("category") Category category,
                         @Param("stock") Stock stock,
                         @Param("visibility") Visibility visibility,
                         @Param("prompt") String prompt,
                         @Param("createdFrom") LocalDateTime createdFrom,
                         @Param("createdTo") LocalDateTime createdTo,
                         @Param("price") Double price,
                         @Param("discount") Long discount,
                         @Param("materialId") Long materialId,
                         @Param("userId") Long userId);




    @Query("""

        SELECT new com.example.jwt_demo.DTOS.Order.OrderAddProducts(p.id,pid.imageUrl, p.productName, p.sku,p.category, p.stockQuantity, p.lowStockThreshold, p.stock,p.price,1L) FROM Product p
        LEFT JOIN ProductImageData pid ON pid.product.id = p.id AND pid.imageLogic = 'Main'
        
   
""")
    List<OrderAddProducts> getAllProductDataForAddNewOrder();


    @Query("""
SELECT new com.example.jwt_demo.DTOS.Order.OrderAddProducts(
    p.id,
    pid.imageUrl,
    p.productName,
    p.sku,
    p.category,
    p.stockQuantity,
    p.lowStockThreshold,
    p.stock,
    p.price,
    op.amountOfProduct
)
FROM OrderProducts op
JOIN op.product p
JOIN op.order o
LEFT JOIN ProductImageData pid 
    ON pid.product.id = p.id AND pid.imageLogic = 'Main'
WHERE o.id = :id
""")
    List<OrderAddProducts> getExistingDataForOrder(@Param("id") Long id);


    @Query("""

            SELECT new com.example.jwt_demo.DTOS.Common.MiniStatHolder(
            count(p.id),
            SUM(CASE WHEN p.visibility = 'Visible' THEN 1 ELSE 0 END),
            SUM(CASE WHEN p.visibility = 'NonVisible' THEN 1 ELSE 0 END),
            SUM(CASE WHEN p.created >= :fromDate AND p.created <= :toDate THEN 1 ELSE 0 END))
         
            FROM Product p


""")
    MiniStatHolder getProductMiniStats(@Param("fromDate") LocalDateTime fromDate, @Param("toDate")LocalDateTime toDate);




}
