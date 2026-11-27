package com.example.jwt_demo.repository;

import com.example.jwt_demo.Entity.MaterialPriceTracker;
import com.example.jwt_demo.Entity.ProductPriceTracker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialPriceTrackerRepository extends JpaRepository<MaterialPriceTracker,Long> {



}
