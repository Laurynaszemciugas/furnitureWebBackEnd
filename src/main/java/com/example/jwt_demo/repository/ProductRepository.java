package com.example.jwt_demo.repository;

import com.example.jwt_demo.Entity.Product;
import com.example.jwt_demo.Enums.Category;
import com.example.jwt_demo.Enums.Stock;
import com.example.jwt_demo.FrontEndModels.ProductFeedModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Long> {

    Optional<Product> findById(Long id);

    @Query("""
SELECT new com.example.jwt_demo.FrontEndModels.ProductFeedModel(
    p.id,
    i.imageUrl,
    p.productName,
    p.category,
    p.price,
    p.stockQuantity,
    p.lowStockThreshold
)
FROM Product p
LEFT JOIN ImagesData i ON i.product.id = p.id and i.imageLogic = 'Main'
Where (:category = 'ALL' OR p.category = :category)
AND (:stock = 'ALL' OR p.stock = :stock)
AND (:prompt = 'ALL' OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :prompt, '%')))
""")
    List<ProductFeedModel> getAllProducts(@Param("category") Category category,@Param("stock") Stock stock,@Param("prompt") String prompt,Pageable pageable);


    @Query("""

    SELECT Ceil(count(p.id) /20) FROM Product p where p.user.id = :id
""")
    Long getProductPages(@Param("id") Long id);

}
