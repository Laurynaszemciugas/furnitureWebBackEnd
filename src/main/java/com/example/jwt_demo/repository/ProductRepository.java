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
JOIN ImagesData i ON i.product.id = p.id
WHERE i.imageLogic = 'Main' and (:category = 'ALL' OR p.category = :category)
""")
    List<ProductFeedModel> getAllProducts(@Param("category") Category category,Pageable pageable);

}
