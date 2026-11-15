package com.example.jwt_demo.repository;

import com.example.jwt_demo.DTOS.Client.CategoryDisplay;
import com.example.jwt_demo.DTOS.Client.ClientCategorys;
import com.example.jwt_demo.DTOS.Client.ClientProductsFilter;
import com.example.jwt_demo.DTOS.Common.GraphDataLongValue;
import com.example.jwt_demo.DTOS.Common.MiniStatHolder;
import com.example.jwt_demo.DTOS.Common.ReportMiniStatHolder;
import com.example.jwt_demo.DTOS.Material.MaterialBriefDto;
import com.example.jwt_demo.DTOS.Order.OrderAddProducts;
import com.example.jwt_demo.DTOS.Product.ProductLowStockList;
import com.example.jwt_demo.DTOS.Product.ProductPerformanceReport;
import com.example.jwt_demo.DTOS.Product.ProductReportPieChart;
import com.example.jwt_demo.Entity.Product;
import com.example.jwt_demo.Enums.Category;
import com.example.jwt_demo.Enums.Stock;
import com.example.jwt_demo.Enums.Visibility;
import com.example.jwt_demo.DTOS.Product.ProductFeedModel;
import org.springframework.data.domain.PageRequest;
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
    p.multipleItems,
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
        WHERE p.user.id = :id
         and p.visibility = 'Visible'
        
   
""")
    List<OrderAddProducts> getAllProductDataForAddNewOrder(Long id);




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
WHERE p.user.id = :userid
 and o.id = :id
""")
    List<OrderAddProducts> getExistingDataForOrder(@Param("id") Long id, Long userid);


    @Query("""

            SELECT new com.example.jwt_demo.DTOS.Common.MiniStatHolder(
            count(p.id),
            SUM(CASE WHEN p.visibility = 'Visible' THEN 1 ELSE 0 END),
            SUM(CASE WHEN p.visibility = 'NonVisible' THEN 1 ELSE 0 END),
            SUM(CASE WHEN p.created >= :fromDate AND p.created <= :toDate THEN 1 ELSE 0 END))
         
            FROM Product p
            
            WHERE p.user.id = :id


""")
    MiniStatHolder getProductMiniStats(@Param("fromDate") LocalDateTime fromDate, @Param("toDate")LocalDateTime toDate, Long id);


    @Query("""

        SELECT p FROM Product p where p.user.id = :userId
        
        
        

""")
    List<Product> getProductsAccordingToUserId(@Param("userId") Long userId);


    // report page stuff

    @Query(value = """
SELECT

    COUNT( CASE
        WHEN o.created >= :currentFrom
        AND o.created <= :currentTo
        THEN o.id
    END),

    COUNT(DISTINCT CASE
        WHEN o.created >= :previousFrom
        AND o.created <= :previousTo
        THEN o.id
    END),

COALESCE((
    SELECT SUM(op2.amount_of_product)
    FROM order_products op2
    JOIN orders o2 ON o2.id = op2.order_id
    WHERE o2.created >= :currentFrom
    AND o2.created <= :currentTo
    GROUP BY op2.product_id
    ORDER BY SUM(op2.amount_of_product) DESC
    LIMIT 1
),0),

COALESCE((
    SELECT p3.product_name
    FROM order_products op3
    JOIN products p3 ON p3.id = op3.product_id
    JOIN orders o3 ON o3.id = op3.order_id
    WHERE o3.created >= :currentFrom
    AND o3.created <= :currentTo
    GROUP BY p3.id, p3.product_name
    ORDER BY SUM(op3.amount_of_product) DESC
    LIMIT 1
),'None'),
    COUNT(DISTINCT CASE
        WHEN o.created >= :currentFrom
        AND o.created <= :currentTo
        AND p.stock = 'Low_Stock'
        THEN p.id
    END),

    COUNT(DISTINCT CASE
        WHEN o.created >= :previousFrom
        AND o.created <= :previousTo
        AND p.stock = 'Low_Stock'
        THEN p.id
    END),

    COALESCE(SUM(
        CASE
            WHEN o.created >= :currentFrom
            AND o.created <= :currentTo
                and o.order_status = 'Finished'
            THEN o.total_price
            ELSE 0
        END
    ),0),

    COALESCE(SUM(
        CASE
            WHEN o.created >= :previousFrom
            AND o.created <= :previousTo
                and o.order_status = 'Finished'
            THEN o.total_price
            ELSE 0
        END
    ),0)

FROM Orders o
JOIN order_products op 
    ON op.order_id = o.id
JOIN products p 
    ON p.id = op.product_id

WHERE o.user_id = :id
 and o.created >= :previousFrom
AND o.created <= :currentTo

""", nativeQuery = true)
    List<Object[]>  getProductReportMiniStats(
            @Param("currentFrom") LocalDateTime currentFrom,
            @Param("currentTo") LocalDateTime currentTo,
            @Param("previousFrom") LocalDateTime previousFrom,
            @Param("previousTo") LocalDateTime previousTo,
            Long id
    );

   @Query("""

     SELECT new com.example.jwt_demo.DTOS.Common.GraphDataLongValue(sum(op.amountOfProduct), p.productName)
      FROM Orders o
        Join productsData op ON op.order.id = o.id
        Join Product p On p.id = op.product.id
        WHERE o.user.id = :id
         and o.created >= :currentFrom
AND o.created <= :currentTo
and o.orderStatus = 'Finished'
        GROUP BY p.id, p.productName
    
        ORDER BY SUM(op.amountOfProduct) DESC
        
        


""")
   List<GraphDataLongValue>  getTopSellingProducts(
           @Param("currentFrom") LocalDateTime currentFrom,
           @Param("currentTo") LocalDateTime currentTo,
           Pageable pageable,
           Long id
   );

    @Query("""

     SELECT new com.example.jwt_demo.DTOS.Product.ProductReportPieChart(
     p.category,sum(op.amountOfProduct))
      FROM Orders o
        Join productsData op ON op.order.id = o.id
        Join Product p On p.id = op.product.id
        WHERE o.user.id = :id
         and o.created >= :currentFrom
        AND o.created <= :currentTo
        and o.orderStatus = 'Finished'
        GROUP BY p.category
    
        
        


""")
    List<ProductReportPieChart>  getProductByCategory(
            @Param("currentFrom") LocalDateTime currentFrom,
            @Param("currentTo") LocalDateTime currentTo,
            Long id
    );

    @Query("""

     SELECT new com.example.jwt_demo.DTOS.Product.ProductLowStockList(
         p.id, pid.imageUrl,p.productName,p.stockQuantity,p.lowStockThreshold,p.stockCalculatedManually)
         FROM Product p
        left Join images pid on pid.product.id = p.id and pid.imageLogic = 'Main'
         WHERE p.user.id = :id
          and p.created >= :currentFrom
        AND p.created <= :currentTo
    
        
        


""")
    List<ProductLowStockList>  getProductLowList(
            @Param("currentFrom") LocalDateTime currentFrom,
            @Param("currentTo") LocalDateTime currentTo,
            Long id
    );

    @Query("""
SELECT new com.example.jwt_demo.DTOS.Product.ProductPerformanceReport(
    p.id,
    pid.imageUrl,
    p.productName,
    COALESCE(SUM(op.amountOfProduct), 0),
    COALESCE(SUM(op.cost * op.amountOfProduct), 0),
    COALESCE(AVG(pc.review), 0)
)
FROM Product p
LEFT JOIN p.images pid
    ON pid.imageLogic = 'Main'
LEFT JOIN OrderProducts op
    ON op.product.id = p.id
LEFT JOIN Orders o
    ON o.id = op.order.id
LEFT JOIN p.comments pc
WHERE o.user.id = :id
 and o.orderStatus = 'Finished'
AND o.created >= :currentFrom
AND o.created <= :currentTo
GROUP BY
    p.id,
    pid.imageUrl,
    p.productName
""")
    List<ProductPerformanceReport> getProductPerformance(
            @Param("currentFrom") LocalDateTime currentFrom,
            @Param("currentTo") LocalDateTime currentTo,
            Long id
    );



    // ====================================================================
    // Client side category
    // ====================================================================

    @Query(value = """
    SELECT p.category, MIN(pid.image_url)
    FROM bpfurniture.products p
    LEFT JOIN bpfurniture.product_image_data pid
        ON pid.product_id = p.id
        AND pid.image_logic = 'Main'
    GROUP BY p.category
    """, nativeQuery = true)
    List<Object[]> getCategoryList(Pageable pageable);



    @Query(value = """
        SELECT
            p.id AS id,
            p.product_name AS productName,
            p.category AS category,
            p.multiple_items AS multiVariants,

            CASE
                WHEN p.multiple_items = FALSE THEN (
                    SELECT pid2.image_url
                    FROM bpfurniture.product_image_data pid2
                    WHERE pid2.product_id = p.id
                      AND pid2.image_logic = 'Main'
                    LIMIT 1
                )
                ELSE NULL
            END AS nonMultiVariantImage,

            CASE
                WHEN p.multiple_items = TRUE
                THEN GROUP_CONCAT(DISTINCT pid.image_url SEPARATOR ', ')
                ELSE NULL
            END AS multiVariantImages,

            CASE
                WHEN p.multiple_items = TRUE
                THEN GROUP_CONCAT(DISTINCT pm.price SEPARATOR ', ')
                ELSE NULL
            END AS multiVariantPrices,

            CASE
                WHEN p.multiple_items = TRUE
                THEN GROUP_CONCAT(DISTINCT pm.discount SEPARATOR ', ')
                ELSE NULL
            END AS multiVariantDiscounts,
                        
            CASE
                WHEN p.multiple_items = TRUE
                THEN GROUP_CONCAT(DISTINCT pm.color SEPARATOR ', ')
                ELSE NULL
            END AS multiVariantDiscounts,            

            CASE
                WHEN p.multiple_items = FALSE THEN p.price
                ELSE NULL
            END AS normalProductPrice,

            CASE
                WHEN p.multiple_items = FALSE THEN p.discount
                ELSE NULL
            END AS normalProductDiscount,
            
            CASE
                WHEN p.multiple_items = FALSE THEN p.color
                ELSE NULL
            END AS normalProductDiscount

        FROM bpfurniture.products p

        LEFT JOIN bpfurniture.product_multiple pm
            ON pm.product_id = p.id

        LEFT JOIN bpfurniture.product_image_data pid
            ON pid.product_id = p.id

        GROUP BY
            p.id,
            p.product_name,
            p.category,
            p.multiple_items,
            p.price,
            p.discount
        """, nativeQuery = true)
    List<Object[]> findProductsClients();


    @Query(value = """
        SELECT
            p.id AS id,
            p.product_name AS productName,
            p.category AS category,
            p.multiple_items AS multiVariants,

            CASE
                WHEN p.multiple_items = FALSE THEN (
                    SELECT pid2.image_url
                    FROM bpfurniture.product_image_data pid2
                    WHERE pid2.product_id = p.id
                      AND pid2.image_logic = 'Main'
                    LIMIT 1
                )
                ELSE NULL
            END AS nonMultiVariantImage,

            CASE
                WHEN p.multiple_items = TRUE
                THEN GROUP_CONCAT(DISTINCT pid.image_url SEPARATOR ', ')
                ELSE NULL
            END AS multiVariantImages,

            CASE
                WHEN p.multiple_items = TRUE
                THEN GROUP_CONCAT(DISTINCT pm.price SEPARATOR ', ')
                ELSE NULL
            END AS multiVariantPrices,

            CASE
                WHEN p.multiple_items = TRUE
                THEN GROUP_CONCAT(DISTINCT pm.discount SEPARATOR ', ')
                ELSE NULL
            END AS multiVariantDiscounts,
                        
            CASE
                WHEN p.multiple_items = TRUE
                THEN GROUP_CONCAT(DISTINCT pm.color SEPARATOR ', ')
                ELSE NULL
            END AS multiVariantDiscounts,            

            CASE
                WHEN p.multiple_items = FALSE THEN p.price
                ELSE NULL
            END AS normalProductPrice,

            CASE
                WHEN p.multiple_items = FALSE THEN p.discount
                ELSE NULL
            END AS normalProductDiscount,
            
            CASE
                WHEN p.multiple_items = FALSE THEN p.color
                ELSE NULL
            END AS normalProductDiscount

        FROM bpfurniture.products p

        LEFT JOIN bpfurniture.product_multiple pm
            ON pm.product_id = p.id

        LEFT JOIN bpfurniture.product_image_data pid
            ON pid.product_id = p.id
                        
        WHERE p.category = :category

        GROUP BY
            p.id,
            p.product_name,
            p.category,
            p.multiple_items,
            p.price,
            p.discount
        """, nativeQuery = true)
    List<Object[]> findProductsClientsWithCategory(String category, Pageable pageable);

    @Query("""
       
      SELECT p FROM Product p
         WHERE p.category = :category


""")
    List<Product> findSimilarItems(String category, Pageable pageable);



    @Query("""

        SELECT new com.example.jwt_demo.DTOS.Client.ClientCategorys(p.category, count(p.category))
         FROM Product p
         group by p.category


""")
    List<ClientCategorys> findClientCategorysAndTheirCount();


    @Query("""

        SELECT distinct(m.materialName)
         FROM Materials m
        


""")
    List<String> findAllAvailableMaterialNames();



    @Query("""

      SELECT p.color FROM Product p
      where p.color is not Null
        


""")
    List<String> findColorNormalProducts();

    @Query("""

      SELECT p.color FROM ProductMultiple p
      where p.color is not Null
        


""")
    List<String> findColorMultiProducts();







    @Query(value = """
SELECT
    p.id,
    p.product_name,
    p.category,
    p.multiple_items,

    (
        SELECT pid.image_url
        FROM product_image_data pid
        WHERE pid.product_id = p.id
          AND pid.image_logic = 'Main'
        LIMIT 1
    ) AS nonMultiVariantImage,

    (
        SELECT GROUP_CONCAT(pid.image_url ORDER BY pm.id)
        FROM product_multiple pm
        JOIN product_image_data pid
            ON pid.product_multiple_id = pm.id
           AND pid.image_logic = 'Main'
        WHERE pm.product_id = p.id
    ) AS multiVariantImages,

    (
        SELECT GROUP_CONCAT(pm.price ORDER BY pm.id)
        FROM product_multiple pm
        WHERE pm.product_id = p.id
    ) AS multiVariantPrices,

    (
        SELECT GROUP_CONCAT(pm.discount ORDER BY pm.id)
        FROM product_multiple pm
        WHERE pm.product_id = p.id
    ) AS multiVariantDiscounts,

    (
        SELECT GROUP_CONCAT(pm.color ORDER BY pm.id)
        FROM product_multiple pm
        WHERE pm.product_id = p.id
    ) AS multiVariantColors,

    p.price,
    p.discount,
    p.color

FROM products p

WHERE

    /* PROMPT */
    (
        :prompt IS NULL
        OR :prompt = ''
        OR LOWER(p.product_name) LIKE LOWER(CONCAT('%', :prompt, '%'))
        OR LOWER(p.description) LIKE LOWER(CONCAT('%', :prompt, '%'))

        OR EXISTS (
            SELECT 1
            FROM product_materials pmat
            JOIN materials m ON m.id = pmat.material_id
            WHERE pmat.product_id = p.id
              AND pmat.product_multiple_id IS NULL
              AND (
                  LOWER(m.material_name) LIKE LOWER(CONCAT('%', :prompt, '%'))
                  OR LOWER(m.description) LIKE LOWER(CONCAT('%', :prompt, '%'))
              )
        )

        OR EXISTS (
            SELECT 1
            FROM extra_details ed
            WHERE ed.product_id = p.id
              AND (
                  LOWER(ed.spec_name) LIKE LOWER(CONCAT('%', :prompt, '%'))
                  OR LOWER(ed.spec_description) LIKE LOWER(CONCAT('%', :prompt, '%'))
              )
        )

        OR EXISTS (
            SELECT 1
            FROM product_multiple pm
            WHERE pm.product_id = p.id
              AND (
                  LOWER(pm.product_name) LIKE LOWER(CONCAT('%', :prompt, '%'))
                  OR LOWER(pm.description) LIKE LOWER(CONCAT('%', :prompt, '%'))
              )
        )
    )

    /* CATEGORY - handles null/empty lists correctly */
    AND (
        :#{#categories == null || #categories.isEmpty()} = true
        OR p.category IN (:categories)
    )

    /* NORMAL PRODUCT FILTERS */
    AND (
        p.multiple_items = 1
        OR (
            (:priceFrom IS NULL OR p.price >= :priceFrom)
            AND (:priceTo IS NULL OR p.price <= :priceTo)
            AND (
                :color IS NULL
                OR LOWER(p.color) LIKE LOWER(CONCAT('%', :color, '%'))
            )
            AND (
                :stock IS NULL
                OR p.stock = :stock
            )
            AND (
                :#{#materials == null || #materials.isEmpty()} = true
                OR EXISTS (
                    SELECT 1
                    FROM product_materials pmat
                    JOIN materials m ON m.id = pmat.material_id
                    WHERE pmat.product_id = p.id
                      AND pmat.product_multiple_id IS NULL
                      AND m.material_name IN (:materials)
                )
            )
        )
    )

    /* VARIANT PRODUCT FILTERS */
    AND (
        p.multiple_items = 0
        OR EXISTS (
            SELECT 1
            FROM product_multiple pm
            WHERE pm.product_id = p.id

              AND (:priceFrom IS NULL OR pm.price >= :priceFrom)
              AND (:priceTo IS NULL OR pm.price <= :priceTo)

              AND (
                  :color IS NULL
                  OR LOWER(pm.color) LIKE LOWER(CONCAT('%', :color, '%'))
              )

              AND (
                  :stock IS NULL
                  OR pm.stock = :stock
              )

              AND (
                  :#{#materials == null || #materials.isEmpty()} = true
                  OR EXISTS (
                      SELECT 1
                      FROM product_materials pmat
                      JOIN materials m ON m.id = pmat.material_id
                      WHERE pmat.product_multiple_id = pm.id
                        AND m.material_name IN (:materials)
                  )
              )
        )
    )

""", nativeQuery = true)
    List<Object[]> queryProductsClientsWithFilters(
            @Param("prompt") String prompt,
            @Param("categories") List<String> categories,
            @Param("priceFrom") Double priceFrom,
            @Param("priceTo") Double priceTo,
            @Param("materials") List<String> materials,
            @Param("color") String color,
            @Param("stock") String stock
    );








}
