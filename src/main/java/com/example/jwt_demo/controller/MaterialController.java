package com.example.jwt_demo.controller;

import com.example.jwt_demo.Common.Logic;
import com.example.jwt_demo.DTOS.Material.MaterialBriefDto;
import com.example.jwt_demo.DTOS.Material.MaterialMiniStat;
import com.example.jwt_demo.DTOS.Product.ComboBoxMaterial;
import com.example.jwt_demo.Entity.Materials;
import com.example.jwt_demo.Entity.ProductJoin.ProductMaterials;
import com.example.jwt_demo.Enums.ActiveInactive;
import com.example.jwt_demo.Enums.MaterialType;
import com.example.jwt_demo.Enums.Stock;
import com.example.jwt_demo.FilterDTO.Material.MaterialFilterHolder;
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


    @PostMapping("/getAllMaterialForFeed")
    public ResponseEntity<List<MaterialBriefDto>> getAllMaterialForFeed(
            @RequestBody MaterialFilterHolder filter
    ) {


        System.out.println("dates" + filter.getFromDateChoice() + " " + filter.getTodDateChoice());

        // MATERIAL TYPE
        if (filter.getMaterialTypeChoice() == MaterialType.ALL) {
            filter.setMaterialTypeChoice(null);
        }

        // ACTIVE / INACTIVE
        if (filter.getActiveInactive() == ActiveInactive.ALL) {
            filter.setActiveInactive(null);
        }

        // STOCK AMOUNT
        if ( filter.getStockAmountChoice() == 0) {
            filter.setStockAmountChoice(null);
        }

        // MIN THRESHOLD
        if (  filter.getMinThresholdChoice() == 0) {
            filter.setMinThresholdChoice(null);
        }

        // UNIT PRICE
        if (filter.getUnitPriceChoice() == 0.0) {
            filter.setUnitPriceChoice(null);
        }

        // FROM DATE
        if (filter.getFromDateChoice().equals(LocalDate.of(1000, 12, 12))) {
            filter.setFromDateChoice(null);
        }

        // TO DATE
        if (filter.getTodDateChoice().equals(LocalDate.of(1000, 12, 12))) {
            filter.setTodDateChoice(null);
        }

        // PROMPT
        if (filter.getPromtChoice().equalsIgnoreCase("ALL")) {
            filter.setPromtChoice(null);
        }

        // STOCK
        if (filter.getStockChoice() == Stock.ALL) {
            filter.setStockChoice(null);
        }

        LocalDateTime from = logic.dateConverter(filter.getFromDateChoice());
        LocalDateTime to =logic.dateConverter(filter.getTodDateChoice());

        return ResponseEntity.ok(
                materialRepository.getExistingMaterialDataForFeed(filter.getMaterialTypeChoice(),
                        filter.getActiveInactive(),
                        filter.getStockAmountChoice(),
                        filter.getMinThresholdChoice(),
                        filter.getUnitPriceChoice(),
                        from,
                        to,
                        filter.getStockChoice(),
                        filter.getPromtChoice(),
                        PageRequest.of(filter.getPage(), filter.getPageCount()))
        );
    }


    @PostMapping("/getTotalPages")
    public ResponseEntity<Long> getAmountOfPages(
            @RequestBody MaterialFilterHolder filter
    ) {


        System.out.println("dates" + filter.getFromDateChoice() + " " + filter.getTodDateChoice());

        if (filter.getMaterialTypeChoice() == MaterialType.ALL) {
            filter.setMaterialTypeChoice(null);
        }

        // ACTIVE / INACTIVE
        if (filter.getActiveInactive() == ActiveInactive.ALL) {
            filter.setActiveInactive(null);
        }

        // STOCK AMOUNT
        if ( filter.getStockAmountChoice() == 0) {
            filter.setStockAmountChoice(null);
        }

        // MIN THRESHOLD
        if (  filter.getMinThresholdChoice() == 0) {
            filter.setMinThresholdChoice(null);
        }

        // UNIT PRICE
        if (filter.getUnitPriceChoice() == 0.0) {
            filter.setUnitPriceChoice(null);
        }

        // FROM DATE
        if (filter.getFromDateChoice().equals(LocalDate.of(1000, 12, 12))) {
            filter.setFromDateChoice(null);
        }

        // TO DATE
        if (filter.getTodDateChoice().equals(LocalDate.of(1000, 12, 12))) {
            filter.setTodDateChoice(null);
        }

        // PROMPT
        if (filter.getPromtChoice().equalsIgnoreCase("ALL")) {
            filter.setPromtChoice(null);
        }

        // STOCK
        if (filter.getStockChoice() == Stock.ALL) {
            filter.setStockChoice(null);
        }

        LocalDateTime from = logic.dateConverter(filter.getFromDateChoice());
        LocalDateTime to =logic.dateConverter(filter.getTodDateChoice());

        Long count = materialRepository.getTotalPages(filter.getMaterialTypeChoice(),
                filter.getActiveInactive(),
                filter.getStockAmountChoice(),
                filter.getMinThresholdChoice(),
                filter.getUnitPriceChoice(),
                from,
                to,
                filter.getStockChoice(),
                filter.getPromtChoice());


        return ResponseEntity.ok(count);
    }



    @GetMapping("/getMaterialMiniStats/{fromDate}/{toDate}")
    public ResponseEntity<MaterialMiniStat> getMaterialMiniStats(@PathVariable LocalDate fromDate, @PathVariable LocalDate toDate){
        return ResponseEntity.ok(materialRepository.getMaterialMiniStats(logic.dateConverter(fromDate),logic.dateConverter(toDate)));
    }



}
