package com.example.jwt_demo.controller;

import com.example.jwt_demo.Common.ErrorResponse;
import com.example.jwt_demo.Common.Logic;
import com.example.jwt_demo.Common.ProvidedDataChecker;
import com.example.jwt_demo.DTOS.Common.MiniStatHolder;
import com.example.jwt_demo.DTOS.Common.ReportMiniStatHolder;
import com.example.jwt_demo.DTOS.Material.MaterialBriefDto;
import com.example.jwt_demo.DTOS.Product.ComboBoxMaterial;
import com.example.jwt_demo.Entity.Materials;
import com.example.jwt_demo.Entity.Orders;
import com.example.jwt_demo.Entity.ProductJoin.ProductMaterials;
import com.example.jwt_demo.Enums.ActiveInactive;
import com.example.jwt_demo.Enums.MaterialType;
import com.example.jwt_demo.Enums.Stock;
import com.example.jwt_demo.Enums.Warnings;
import com.example.jwt_demo.FilterDTO.Material.MaterialFilterHolder;
import com.example.jwt_demo.FilterDTO.Order.OrderFilterHolder;
import com.example.jwt_demo.GlobalExseptions.Exseptions.ValidationException;
import com.example.jwt_demo.repository.MaterialRepository;
import com.example.jwt_demo.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/material")
public class MaterialController {

    @Autowired
    MaterialRepository materialRepository;

    @Autowired
    Common common;

    @Autowired
    Logic logic;

    @Autowired
    ProvidedDataChecker providedDataChecker;


    @GetMapping("/getMaterialNames")
    public ResponseEntity<List<ComboBoxMaterial>> getMaterialNames(){

        CustomUserDetails user = common.getUserData();
        if(user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }


        return ResponseEntity.ok(materialRepository.getAllMaterialNames(user.getId()));


    }

    // for quick price analization on textfield
    @PostMapping("/getEstimatedPriceOfMaterialUsed")
    public ResponseEntity<Double> getEstimatedMaterialCost(@RequestBody List<ProductMaterials> productMaterials){

        CustomUserDetails user = common.getUserData();
        if(user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

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


    @PostMapping("/getAllMaterialForFeed")
    public ResponseEntity<List<MaterialBriefDto>> getAllMaterialForFeed(
            @RequestBody MaterialFilterHolder filter
    ) {

        filter = providedDataChecker.defaultValueChecker(filter, MaterialFilterHolder.class);


        return ResponseEntity.ok(
                materialRepository.getExistingMaterialDataForFeed(filter.getMaterialTypeChoice(),
                        filter.getActiveInactive(),
                        filter.getStockAmountChoice(),
                        filter.getMinThresholdChoice(),
                        filter.getUnitPriceChoice(),
                        logic.dateConverter(filter.getFromDateChoice()),
                        logic.dateConverter(filter.getTodDateChoice()),
                        filter.getStockChoice(),
                        filter.getPromtChoice(),
                        PageRequest.of(filter.getPage(), filter.getPageCount()))
        );
    }


    @PostMapping("/getTotalPages")
    public ResponseEntity<Long> getAmountOfPages(
            @RequestBody MaterialFilterHolder filter
    ) {


        filter = providedDataChecker.defaultValueChecker(filter, MaterialFilterHolder.class);

        Long count = materialRepository.getTotalPages(filter.getMaterialTypeChoice(),
                filter.getActiveInactive(),
                filter.getStockAmountChoice(),
                filter.getMinThresholdChoice(),
                filter.getUnitPriceChoice(),
                logic.dateConverter(filter.getFromDateChoice()),
                logic.dateConverter(filter.getTodDateChoice()),
                filter.getStockChoice(),
                filter.getPromtChoice());


        return ResponseEntity.ok(count);
    }



    @GetMapping("/getMaterialMiniStats/{fromDate}/{toDate}")
    public ResponseEntity<MiniStatHolder> getMaterialMiniStats(@PathVariable LocalDate fromDate, @PathVariable LocalDate toDate){
        return ResponseEntity.ok(materialRepository.getMaterialMiniStats(logic.dateConverter(fromDate),logic.dateConverter(toDate)));
    }


    @PostMapping("/createNewMaterial")
    public ResponseEntity<ErrorResponse> createNewMaterial(@RequestBody Materials mat){


        // checks if there is any null or is empty values
        providedDataChecker.checkEmptyValue(mat, Materials.class);

        Materials newMat = new Materials();


        // ============ Name ============================
        // later check with AI if this is not bad
        newMat.setMaterialName(mat.getMaterialName());

        // ============ in stock value ============================

        newMat.setInStock(mat.getInStock());


        // ============ in min threshold value ============================


        newMat.setMinThresHold(mat.getMinThresHold());

        if (newMat.getInStock() == 0) {
            newMat.setStock(Stock.No_Stock);
        } else if (newMat.getInStock() <= newMat.getMinThresHold()) {
            newMat.setStock(Stock.Low_Stock);
        } else {
            newMat.setStock(Stock.In_Stock);
        }

        // ============ in min setEnabled value ============================
        // default value
        newMat.setEnabled(ActiveInactive.ACTIVE);



        // check later with AI
        newMat.setDescription(mat.getDescription());

        newMat.setMaterialType(mat.getMaterialType());

        //newMat.setUser();

        newMat.setMaterialUrl(mat.getMaterialUrl());

        newMat.setCareInstructions(mat.getCareInstructions());

        newMat.setMaterialColor(mat.getMaterialColor());


        if (mat.getImages() != null) {
            for (var img : mat.getImages()) {
                img.setMaterials(newMat);
                newMat.getImages().add(img);
            }
        }

        newMat.setImages(newMat.getImages());

        materialRepository.save(newMat);

        return ResponseEntity.ok(new ErrorResponse(mat.getMaterialName() + " Material saved successfully", Warnings.OK));
    }


    @PostMapping("/editExistingMaterial")
    public ResponseEntity<ErrorResponse> ediMaterial(@RequestBody Materials mat){


        // checks if there is any null or is empty values
        providedDataChecker.checkEmptyValue(mat, Materials.class);

        Materials existingMat = materialRepository.findById(mat.getId()).orElseThrow();


        // ============ Name ============================
        // later check with AI if this is not bad
        existingMat.setMaterialName(mat.getMaterialName());

        // ============ in stock value ============================

        existingMat.setInStock(mat.getInStock());


        // ============ in min threshold value ============================


        existingMat.setMinThresHold(mat.getMinThresHold());

        if (existingMat.getInStock() == 0) {
            existingMat.setStock(Stock.No_Stock);
        } else if (existingMat.getInStock() <= existingMat.getMinThresHold()) {
            existingMat.setStock(Stock.Low_Stock);
        } else {
            existingMat.setStock(Stock.In_Stock);
        }

        // ============ in min setEnabled value ============================
        // default value
        existingMat.setEnabled(ActiveInactive.ACTIVE);



        // check later with AI
        existingMat.setDescription(mat.getDescription());

        existingMat.setMaterialType(mat.getMaterialType());

        //newMat.setUser();

        existingMat.setMaterialUrl(mat.getMaterialUrl());

        existingMat.setCareInstructions(mat.getCareInstructions());

        existingMat.setMaterialColor(mat.getMaterialColor());

        existingMat.setMaterialTextures(mat.getMaterialTextures());
        existingMat.setMaterialTextures(mat.getMaterialTextures());
        existingMat.setMaterialGrainPatterns(mat.getMaterialGrainPatterns());
        existingMat.setUnitPrice(mat.getUnitPrice());
        existingMat.setMaterialWeight(mat.getMaterialWeight());
        existingMat.setMaterialFinishType(mat.getMaterialFinishType());
        existingMat.setUnit(mat.getUnit());
        existingMat.setDeliveryDate(mat.getDeliveryDate());
        existingMat.setDefaultTimePeriod(mat.getDefaultTimePeriod());


        if (mat.getImages() != null) {
            for (var img : mat.getImages()) {
                img.setMaterials(existingMat);
                existingMat.getImages().add(img);
            }
        }

        existingMat.setImages(existingMat.getImages());

        materialRepository.save(existingMat);

        return ResponseEntity.ok(new ErrorResponse(mat.getMaterialName() + " Material edited successfully", Warnings.OK));
    }


    @GetMapping("/deleteMaterial/{id}")
    public ResponseEntity<ErrorResponse> deleteMaterial(@PathVariable Long id){

        Materials material = materialRepository.findById(id).orElseThrow();

        try{

            materialRepository.deleteById(id);

        }catch (Exception e){


            material.setEnabled(ActiveInactive.INACTIVE);

            materialRepository.save(material);

            return ResponseEntity.ok(new ErrorResponse(  material.getMaterialName() + " was set to Inactive successfully", Warnings.OK));

        }
        return ResponseEntity.ok(new ErrorResponse(material.getMaterialName() + "Material removed successfully", Warnings.OK));
    }


    @GetMapping("/getMaterial/{id}")
    public ResponseEntity<Materials> getEditData(@PathVariable Long id){

        Materials material = materialRepository.findById(id).orElseThrow();


        return ResponseEntity.ok(material);
    }


    // report page calls

    @GetMapping("/getProductsMiniStatData/{fromDate}/{toDate}")
    public ResponseEntity<ReportMiniStatHolder> getOrderMiniStatData(@PathVariable LocalDate fromDate, @PathVariable LocalDate toDate){

        LocalDate preFrom = fromDate.withDayOfMonth(1).minusMonths(1);

        LocalDate preTo = preFrom.plusMonths(1).minusDays(1);


        return ResponseEntity.ok(materialRepository.getProductMiniStats(logic.dateConverter(fromDate),logic.dateConverter(toDate),logic.dateConverter(preFrom),logic.dateConverter(preTo)));

    }




}
