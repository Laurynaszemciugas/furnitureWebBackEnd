package com.example.jwt_demo.repository;

import com.example.jwt_demo.DTOS.Material.MaterialBriefDto;
import com.example.jwt_demo.DTOS.Order.OrderAddProducts;
import com.example.jwt_demo.Entity.Product;
import com.example.jwt_demo.Enums.Category;
import com.example.jwt_demo.Enums.Stock;
import com.example.jwt_demo.Enums.Visibility;
import com.example.jwt_demo.DTOS.Product.ProductFeedModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
    p.price - (p.price * p.discount /100.0),
    p.visibility
    
)
FROM Product p
LEFT JOIN ProductImageData i ON i.product.id = p.id and i.imageLogic = 'Main' AND p.user.id = :id
WHERE p.user.id = :id
AND (:category = 'ALL' OR p.category = :category)
AND (:stock = 'ALL' OR p.stock = :stock)
AND (:vis = 'ALL' OR  p.visibility = :vis)
AND (:prompt = 'ALL' OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :prompt, '%')))

""")
    List<ProductFeedModel> getAllProducts(@Param("category") Category category, @Param("stock") Stock stock, @Param("prompt") String prompt, @Param("vis") Visibility vis, @Param("id") Long id, Pageable pageable);


    @Query("""

    SELECT CASE  WHEN COUNT(p.id) = 0 THEN 1 ELSE CEIL(COUNT(p.id) / 20.0) END
    FROM Product p
WHERE p.user.id = :id
AND (:category = 'ALL' OR p.category = :category)
AND (:stock = 'ALL' OR p.stock = :stock)
AND (:vis = 'ALL' OR  p.visibility = :vis)
AND (:prompt = 'ALL' OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :prompt, '%')))
    
    
    
""")
    Long getProductPages(@Param("category") Category category, @Param("stock") Stock stock, @Param("prompt") String prompt, @Param("vis") Visibility vis,@Param("id") Long id);




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







}
