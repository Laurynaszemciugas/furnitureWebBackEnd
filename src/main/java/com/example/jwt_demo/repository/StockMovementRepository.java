package com.example.jwt_demo.repository;

import com.example.jwt_demo.DTOS.StockMovement.StockMovementGrid;
import com.example.jwt_demo.Entity.StockMovement;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement,Long> {



@Query("""
    SELECT new com.example.jwt_demo.DTOS.StockMovement.StockMovementGrid(
    
     st.created, m.materialName, st.type, st.amountTakeAdd,st.balance)
     
     
    FROM StockMovement st
    
    Join Materials m ON m.id = st.materials.id
    WHERE m.user.id = :id
     and st.created >= :dateFrom
      AND st.created <= :dateTo
    ORDER BY st.created DESC

""")

List<StockMovementGrid> stockMovementHistory(@Param("dateFrom") LocalDateTime dateFrom,
                                             @Param("dateTo") LocalDateTime dateTo, Pageable pageable,
                                             Long id);


}
