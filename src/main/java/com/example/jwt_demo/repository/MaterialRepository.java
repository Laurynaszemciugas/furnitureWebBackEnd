package com.example.jwt_demo.repository;

import com.example.jwt_demo.DTOS.Common.GraphDataDateValue;
import com.example.jwt_demo.DTOS.Common.MiniStatHolder;
import com.example.jwt_demo.DTOS.Common.ReportMiniStatHolder;
import com.example.jwt_demo.DTOS.Material.MaterialBriefDto;
import com.example.jwt_demo.DTOS.Material.MaterialReportPieChart;
import com.example.jwt_demo.DTOS.Order.OrderReportPieChart;
import com.example.jwt_demo.DTOS.Product.ComboBoxMaterial;
import com.example.jwt_demo.Entity.Materials;
import com.example.jwt_demo.Enums.ActiveInactive;
import com.example.jwt_demo.Enums.MaterialType;
import com.example.jwt_demo.Enums.Stock;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MaterialRepository extends JpaRepository<Materials,Long> {


    @Query("SELECT new com.example.jwt_demo.DTOS.Product.ComboBoxMaterial(m.id, m.materialName) " +
            "FROM Materials m " +
            "WHERE m.user.id = :id")
    List<ComboBoxMaterial> getAllMaterialNames(@Param("id") Long id);

    @Query("""

    SELECT m FROM Materials m where m.id = :materialID and m.user.id = :id

""")
    Materials findByMaterialName(@Param("materialID") Long materialID, @Param("id") Long id);



    @Query("""
SELECT new com.example.jwt_demo.DTOS.Material.MaterialBriefDto(
    
    m.id,
    mid.imageUrl,
    m.materialName,
    m.description,
    m.enabled,
    m.materialType,
    m.stock,
    m.inStock,
    m.minThresHold,
    m.unitPrice,
    m.created
)
FROM Materials m
LEFT JOIN MaterialImageData mid 
    ON mid.materials.id = m.id 
   AND mid.imageLogic = 'Main'
WHERE (:materialTypeChoice IS NULL OR m.materialType = :materialTypeChoice)
  AND (:enabled IS NULL OR m.enabled = :enabled)
  AND (:stockAmountChoice IS NULL OR m.inStock = :stockAmountChoice)
  AND (:minThresholdChoice IS NULL OR m.minThresHold = :minThresholdChoice)
  AND (:unitPriceChoice IS NULL OR m.unitPrice = :unitPriceChoice)
  AND (:fromDateChoice IS NULL OR m.created >= :fromDateChoice)
  AND (:todDateChoice IS NULL OR m.created <= :todDateChoice)
  AND (:stockChoice IS NULL OR m.stock = :stockChoice)
  AND (
        :promtChoice IS NULL OR
        LOWER(m.materialName) LIKE LOWER(CONCAT('%', :promtChoice, '%')) OR
        LOWER(m.description) LIKE LOWER(CONCAT('%', :promtChoice, '%')) OR
        LOWER(CONCAT(m.materialName, ' ', m.description))
            LIKE LOWER(CONCAT('%', :promtChoice, '%'))
      )
""")
    List<MaterialBriefDto> getExistingMaterialDataForFeed(
            @Param("materialTypeChoice") MaterialType materialTypeChoice,
            @Param("enabled") ActiveInactive enabled,
            @Param("stockAmountChoice") Long stockAmountChoice,
            @Param("minThresholdChoice") Long minThresholdChoice,
            @Param("unitPriceChoice") Double unitPriceChoice,
            @Param("fromDateChoice") LocalDateTime fromDateChoice,
            @Param("todDateChoice") LocalDateTime todDateChoice,
            @Param("stockChoice") Stock stockChoice,
            @Param("promtChoice") String promtChoice,
            Pageable pageable
    );


    @Query("""
SELECT
    CASE
        WHEN COUNT(DISTINCT m.id) = 0
        THEN 1
        ELSE CEIL(COUNT(DISTINCT m.id) / 5.0)
    END
FROM Materials m
LEFT JOIN MaterialImageData mid
    ON mid.materials.id = m.id
   AND mid.imageLogic = 'Main'
WHERE (:materialTypeChoice IS NULL OR m.materialType = :materialTypeChoice)
  AND (:enabled IS NULL OR m.enabled = :enabled)
  AND (:stockAmountChoice IS NULL OR m.inStock = :stockAmountChoice)
  AND (:minThresholdChoice IS NULL OR m.minThresHold = :minThresholdChoice)
  AND (:unitPriceChoice IS NULL OR m.unitPrice = :unitPriceChoice)
  AND (:fromDateChoice IS NULL OR m.created >= :fromDateChoice)
  AND (:todDateChoice IS NULL OR m.created <= :todDateChoice)
  AND (:stockChoice IS NULL OR m.stock = :stockChoice)
  AND (
        :promtChoice IS NULL OR
        LOWER(m.materialName) LIKE LOWER(CONCAT('%', :promtChoice, '%')) OR
        LOWER(m.description) LIKE LOWER(CONCAT('%', :promtChoice, '%')) OR
        LOWER(CONCAT(m.materialName, ' ', m.description))
            LIKE LOWER(CONCAT('%', :promtChoice, '%'))
      )
""")
    Long getTotalPages(
            MaterialType materialTypeChoice,
            ActiveInactive enabled,
            Long stockAmountChoice,
            Long minThresholdChoice,
            Double unitPriceChoice,
            LocalDateTime fromDateChoice,
            LocalDateTime todDateChoice,
            Stock stockChoice,
            String promtChoice
    );



    @Query("""

            SELECT new com.example.jwt_demo.DTOS.Common.MiniStatHolder(
            count(m.id),
            SUM(CASE WHEN m.enabled = 'ACTIVE' THEN 1 ELSE 0 END),
            SUM(CASE WHEN m.enabled = 'INACTIVE' THEN 1 ELSE 0 END),
            SUM(CASE WHEN m.created >= :fromDate AND m.created <= :toDate THEN 1 ELSE 0 END))
         
            FROM Materials m


""")
    MiniStatHolder getMaterialMiniStats(@Param("fromDate")LocalDateTime fromDate, @Param("toDate")LocalDateTime toDate);


    // report page stuff

    @Query("""
    SELECT new com.example.jwt_demo.DTOS.Common.ReportMiniStatHolder(

        COUNT(DISTINCT o.id) FILTER (
            WHERE o.created >= :currentFrom
              AND o.created < :currentTo
        ),

        COUNT(DISTINCT o.id) FILTER (
            WHERE o.created >= :previousFrom
              AND o.created < :previousTo
        ),
        
        
            COUNT(DISTINCT o.id) FILTER (
            WHERE o.created >= :currentFrom
              AND o.created < :currentTo
              AND o.stock = 'In_Stock'
        ),

        COUNT(DISTINCT o.id) FILTER (
            WHERE o.created >= :previousFrom
              AND o.created < :previousTo
              AND o.stock = 'In_Stock'
        ),
        

        COUNT(DISTINCT o.id) FILTER (
            WHERE o.created >= :currentFrom
              AND o.created < :currentTo
              AND o.stock = 'Low_Stock'
        ),

        COUNT(DISTINCT o.id) FILTER (
            WHERE o.created >= :previousFrom
              AND o.created < :previousTo
              AND o.stock = 'Low_Stock'
        ),



        COALESCE(
            SUM(o.unitPrice * o.inStock) FILTER (
                WHERE o.created >= :currentFrom
                  AND o.created < :currentTo
            ),
            0.0
        ),

        COALESCE(
            SUM(o.unitPrice * o.inStock) FILTER (
                WHERE o.created >= :previousFrom
                  AND o.created < :previousTo
            ),
            0.0
        )

    )
    FROM Materials o
    WHERE o.created >= :previousFrom
      AND o.created < :currentTo
""")
    ReportMiniStatHolder getProductMiniStats(
            @Param("currentFrom") LocalDateTime currentFrom,
            @Param("currentTo") LocalDateTime currentTo,
            @Param("previousFrom") LocalDateTime previousFrom,
            @Param("previousTo") LocalDateTime previousTo
    );

    @Query("""
    SELECT new com.example.jwt_demo.DTOS.Material.MaterialReportPieChart(
        COALESCE(SUM(CASE
            WHEN o.stock = 'In_Stock' THEN 1L ELSE 0L
        END), 0),

        COALESCE(SUM(CASE
            WHEN o.stock = 'Low_Stock' THEN 1L ELSE 0L
        END), 0),

        COALESCE(SUM(CASE
            WHEN o.stock = 'No_Stock' THEN 1L ELSE 0L
        END), 0)

    )
    FROM Materials o
    WHERE o.created >= :dateFrom
      AND o.created < :dateTo
""")
    MaterialReportPieChart MaterialReportPieChart(
            @Param("dateFrom") LocalDateTime dateFrom,
            @Param("dateTo") LocalDateTime dateTo
    );


    @Query("""

    SELECT new com.example.jwt_demo.DTOS.Common.GraphDataDateValue(
    o.createdDate,
    SUM(op.cost * op.amountOfProduct))
    FROM Orders o
    JOIN productsData op
    WHERE o.created >= :dateFrom
    AND o.created <= :dateTo
    GROUP BY o.createdDate
    ORDER BY createdDate
    
    

""")
    List<GraphDataDateValue> productReportLineBar(@Param("dateFrom") LocalDateTime dateFrom,
                                                @Param("dateTo") LocalDateTime dateTo);




}
