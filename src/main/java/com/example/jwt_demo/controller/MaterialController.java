package com.example.jwt_demo.controller;

import com.example.jwt_demo.DTOS.Product.ComboBoxMaterial;
import com.example.jwt_demo.Entity.Materials;
import com.example.jwt_demo.Entity.ProductJoin.ProductMaterials;
import com.example.jwt_demo.repository.MaterialRepository;
import com.example.jwt_demo.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/material")
public class MaterialController {

    @Autowired
    MaterialRepository materialRepository;

    @Autowired
    Common common;


    @GetMapping("/getMaterialNames")
    public ResponseEntity<List<ComboBoxMaterial>> getMaterialNames(){

        CustomUserDetails user = common.getUserData();

        System.out.println(user.getUsername());

        return ResponseEntity.ok(materialRepository.getAllMaterialNames(user.getId()));


    }

    // for quick price analization on textfield
    @PostMapping("/getEstimatedPriceOfMaterialUsed")
    public ResponseEntity<Double> getEstimatedMaterialCost(@RequestBody List<ProductMaterials> productMaterials){

        CustomUserDetails user = common.getUserData();

        double estimatedPrice = 0.0;

        if(productMaterials != null && !productMaterials.isEmpty()) {
            for (var s : productMaterials) {
                Materials usedMaterial = materialRepository.findByMaterialName(s.getId(), user.getId());
                estimatedPrice += (usedMaterial.getUnitPrice() * s.getAmountUsed());
            }

            return  ResponseEntity.ok(estimatedPrice);
        }
        else{
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(0.0);
        }
    }

    @PostMapping("/getMaterialDataAccordingToName")
    public ResponseEntity<Materials> getMaterialDataToName(@RequestBody Long name){

        CustomUserDetails user = common.getUserData();

        Materials usedMaterial = materialRepository.findByMaterialName(name, user.getId());

        if(usedMaterial !=null){
            return  ResponseEntity.ok(usedMaterial);
        }

        return ResponseEntity.ok(new Materials());

    }


    @PostMapping("/checkIfMaterialsAmountIsInStock")
    public ResponseEntity<String> checkIfMaterialsDontExceedStockLimtis(@RequestBody List<ProductMaterials> productMaterials){

        CustomUserDetails user = common.getUserData();

        List<String> errorMessage = new ArrayList<>();

        if(productMaterials != null && !productMaterials.isEmpty()) {
            for (var s : productMaterials) {

                Materials usedMaterial = materialRepository.findByMaterialName(s.getId(), user.getId());
                if(usedMaterial.getInStock() == 0){
                    errorMessage.add(String.format("%s %s %d %s %d",s.getNameForRefrence(),"will not be able to produce due to  taken material taken:",s.getAmountUsed(),"being too much for existing stock", usedMaterial.getInStock()));
                }
                if (usedMaterial.getInStock() - s.getAmountUsed() <= 2){
                    errorMessage.add(String.format("%s %s %s %d","Product:", s.getNameForRefrence(), "will deplete stock current stock: ", usedMaterial.getInStock()));
                }

            }
        }


        String itemsThatWillCauseProblems = String.join("  |  ",errorMessage);




        return  ResponseEntity.ok(itemsThatWillCauseProblems);
    }



}
