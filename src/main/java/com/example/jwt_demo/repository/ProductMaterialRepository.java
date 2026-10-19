package com.example.jwt_demo.repository;

import com.example.jwt_demo.Entity.ProductJoin.ProductMaterials;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductMaterialRepository extends JpaRepository<ProductMaterials,Long> {
}
