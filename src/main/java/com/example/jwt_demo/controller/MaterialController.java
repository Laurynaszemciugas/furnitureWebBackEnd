package com.example.jwt_demo.controller;

import com.example.jwt_demo.Common.ErrorResponse;
import com.example.jwt_demo.Common.Logic;
import com.example.jwt_demo.Common.ProvidedDataChecker;
import com.example.jwt_demo.DTOS.Common.MiniStatHolder;
import com.example.jwt_demo.DTOS.Material.MaterialBriefDto;
import com.example.jwt_demo.DTOS.Product.ComboBoxMaterial;
import com.example.jwt_demo.Entity.Materials;
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

        System.out.println(mat.getMaterialName());
        System.out.println(mat.getMaterialColor());

        Materials newMat = new Materials();

        // ============ Name ============================
        // later check with AI if this is not bad
        newMat.setMaterialName(mat.getMaterialName());

        // ============ in stock value ============================
        if(newMat.getInStock() < 0){
            throw new ValidationException("Stock level can not be less than 0", Warnings.ERROR);
        }
        newMat.setInStock(mat.getInStock());


        // ============ in min threshold value ============================
        if(newMat.getMinThresHold() < 0){
            throw new ValidationException("MIn threshold level can not be less than 0", Warnings.ERROR);
        }

        newMat.setMinThresHold(mat.getMinThresHold());

        if(mat.getInStock() > mat.getMinThresHold()) {
            newMat.setStock(Stock.In_Stock);
        }
        if(mat.getInStock() <= mat.getMinThresHold()){
            newMat.setStock(Stock.Low_Stock);
        }
        if(mat.getInStock() == 0){
            newMat.setStock(Stock.No_Stock);
        }

        // ============ in min setEnabled value ============================
        // default value
        newMat.setEnabled(ActiveInactive.ACTIVE);

        // ============ in material weight value ============================
        if(newMat.getMaterialWeight() <= 0){
            throw new ValidationException("Material weight can not be less or equal 0 grams", Warnings.ERROR);
        }

        // ============ in unit price weight value ============================
        if(newMat.getUnitPrice() <= 0){
            throw new ValidationException("Unit price can not be less or equal 0", Warnings.ERROR);
        }

        // ============ in unit price weight value ============================
        if(newMat.getUnit() == null){
            throw new ValidationException("Unit name cannot be empty", Warnings.ERROR);
        }

        // check later with AI
        newMat.setDescription(mat.getDescription());

        newMat.setMaterialType(mat.getMaterialType());

        //newMat.setUser();

        newMat.setMaterialUrl(mat.getMaterialUrl());

        newMat.setCareInstructions(mat.getCareInstructions());

        newMat.setMaterialColor(mat.getMaterialColor());


        if (mat.getImages() != null) {
            for (var img : mat.getImages()) {
                img.setMaterials(mat);
                newMat.getImages().add(img);
            }
        }

        mat.setImages(newMat.getImages());

        materialRepository.save(mat);

        return ResponseEntity.ok(new ErrorResponse(mat.getMaterialName() + " Material saved successfully", Warnings.OK));
    }



}
