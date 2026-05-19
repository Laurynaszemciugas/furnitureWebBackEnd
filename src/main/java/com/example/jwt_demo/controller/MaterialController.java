package com.example.jwt_demo.controller;

import com.example.jwt_demo.Entity.Materials;
import com.example.jwt_demo.Entity.ProductJoin.ProductMaterials;
import com.example.jwt_demo.repository.MaterialRepository;
import com.example.jwt_demo.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/material")
public class MaterialController {

    @Autowired
    MaterialRepository materialRepository;

    @GetMapping("/getMaterialNames")
    public ResponseEntity<List<String>> getMaterialNames(){

        CustomUserDetails user =
                (CustomUserDetails) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        System.out.println(user.getUsername());

        return ResponseEntity.ok(materialRepository.getAllMaterialNames(user.getId()));


    }

    // for quick price analization on textfield
    @PostMapping("/getEstimatedPriceOfMaterialUsed")
    public ResponseEntity<Double> getEstimatedMaterialCost(@RequestBody List<ProductMaterials> productMaterials){

        double estimatedPrice = 0.0;

        if(productMaterials != null || !productMaterials.isEmpty()) {
            for (var s : productMaterials) {
                Materials usedMaterial = materialRepository.findByMaterialName(s.getNameForRefrence());
                estimatedPrice += (usedMaterial.getUnitPrice() * s.getAmountUsed());
            }

            return  ResponseEntity.ok(estimatedPrice);
        }
        else{
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(0.0);
        }
    }

    @PostMapping("/getMaterialDataAccordingToName")
    public ResponseEntity<Materials> getMaterialDataToName(@RequestBody String name){

        System.out.println(name + " name to find");
        Materials usedMaterial = materialRepository.findByMaterialName(name);

        System.out.println(usedMaterial.getMaterialName() + " name found ?");

            return  ResponseEntity.ok(usedMaterial);
    }



}
