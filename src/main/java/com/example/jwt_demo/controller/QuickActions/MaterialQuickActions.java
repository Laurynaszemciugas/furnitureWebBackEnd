package com.example.jwt_demo.controller.QuickActions;

import com.example.jwt_demo.Common.ErrorResponse;
import com.example.jwt_demo.Entity.Materials;
import com.example.jwt_demo.Enums.Warnings;
import com.example.jwt_demo.FilterDTO.Order.OrderFilterHolder;
import com.example.jwt_demo.GlobalExseptions.Exseptions.ValidationException;
import com.example.jwt_demo.controller.Common;
import com.example.jwt_demo.repository.MaterialRepository;
import com.example.jwt_demo.repository.OrderRepository;
import com.example.jwt_demo.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quickMaterialActions")
public class MaterialQuickActions {

    @Autowired
    MaterialRepository materialRepository;

    @Autowired
    Common common;

    @GetMapping("/updateMaterialStock/{materialId}/{NewStock}")
    public ResponseEntity<ErrorResponse> updateMaterialStock(@PathVariable Long materialId,@PathVariable Long NewStock) {

        CustomUserDetails user = common.getUserData();

        Materials materials = materialRepository.findById(materialId).orElseThrow();

        if(materials.getUser().getId() != user.getId()){
            throw new ValidationException("Something went wrong",Warnings.ERROR);
        }


        Long stock = materials.getInStock();

        Long updatedStock = stock + NewStock;

        if(updatedStock < 0){
            throw new ValidationException("Cannot update material stock is lower than 0",Warnings.ERROR);
        }

        materials.setInStock(updatedStock);

        materialRepository.save(materials);


        materialRepository.recalculteStockJustAccordingToLowThreAndStock(user.getId());


        return ResponseEntity.ok(new ErrorResponse("Material updated", Warnings.OK));

    }

}
