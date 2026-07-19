package com.example.jwt_demo.repository;

import com.example.jwt_demo.Entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockMovementRepository extends JpaRepository<StockMovement,Long> {




}
