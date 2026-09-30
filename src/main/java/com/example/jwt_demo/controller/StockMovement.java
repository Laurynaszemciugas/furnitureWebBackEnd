package com.example.jwt_demo.controller;

import com.example.jwt_demo.DTOS.Product.ComboBoxMaterial;
import com.example.jwt_demo.DTOS.StockMovement.StockMovementGrid;
import com.example.jwt_demo.Entity.Materials;
import com.example.jwt_demo.Enums.Warnings;
import com.example.jwt_demo.GlobalExseptions.Exseptions.ValidationException;
import com.example.jwt_demo.repository.MaterialRepository;
import com.example.jwt_demo.repository.StockMovementRepository;
import com.example.jwt_demo.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stockMovement")
public class StockMovement {


    @Autowired
    Common common;

    @Autowired
    MaterialRepository materialRepository;

    @Autowired
    StockMovementRepository stockMovementRepository;

    @GetMapping("/getAllStockMovement/{materialId}")
    public ResponseEntity<List<StockMovementGrid>> getAllStockMovement(@PathVariable Long materialId){

        CustomUserDetails user = common.getUserData();
        Materials materials = materialRepository.findById(materialId).orElseThrow();
        if(user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if(!user.getId().equals(materials.getUser().getId())){
            throw new ValidationException("Something went wrong", Warnings.ERROR);
        }


        return ResponseEntity.ok(stockMovementRepository.stockMovementHistoryAll(materialId));


    }


}
