package com.example.jwt_demo.repository;

import com.example.jwt_demo.Entity.Materials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MaterialRepository extends JpaRepository<Materials,Long> {


    @Query("""

        SELECT m.materialName FROM Materials m where m.user.id = :id

""")
    List<String> getAllMaterialNames(@Param("id") Long id);



}
