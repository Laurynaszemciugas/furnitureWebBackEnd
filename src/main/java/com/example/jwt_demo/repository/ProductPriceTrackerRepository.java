package com.example.jwt_demo.repository;

import com.example.jwt_demo.Entity.Notifications;
import com.example.jwt_demo.Entity.ProductPriceTracker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductPriceTrackerRepository extends JpaRepository<ProductPriceTracker,Long> {



}
