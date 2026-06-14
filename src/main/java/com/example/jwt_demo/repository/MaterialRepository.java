package com.example.jwt_demo.repository;

import com.example.jwt_demo.DTOS.Material.MaterialBriefDto;
import com.example.jwt_demo.DTOS.Product.ComboBoxMaterial;
import com.example.jwt_demo.Entity.Materials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

       SELECT new com.example.jwt_demo.DTOS.Material.MaterialBriefDto(mid.imageUrl, m.materialName, m.description, m.enabled, m.materialType, m.stock, m.inStock, m.minThresHold,m.unitPrice,m.created)
       FROM Materials m
       LEFT JOIN MaterialImageData mid ON mid.materials.id = m.id AND mid.imageLogic = 'Main'

""")
    List<MaterialBriefDto> getExistingMaterialDataForFeed();



}
