package com.example.jwt_demo.repository;

import com.example.jwt_demo.Entity.ProductMultiple;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MultipleProductRepository extends JpaRepository<ProductMultiple, Long> {
}
