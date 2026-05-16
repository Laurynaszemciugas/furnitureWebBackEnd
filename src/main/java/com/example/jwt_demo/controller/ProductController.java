package com.example.jwt_demo.controller;


import com.example.jwt_demo.Entity.Product;
import com.example.jwt_demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    ProductRepository productRepository;

    @PostMapping("/saveProduct")
    public ResponseEntity<String> saveProduct(@RequestBody Product product){

        System.out.println("Processing incoming product save request...");

        Product cleanProduct = new Product();

        cleanProduct.setId(product.getId());
        cleanProduct.setProductName(product.getProductName());
        cleanProduct.setSku(product.getSku());
        cleanProduct.setDescription(product.getDescription());
        cleanProduct.setPrice(product.getPrice());
        cleanProduct.setDiscount(product.getDiscount());
        cleanProduct.setMaterialCost(product.getMaterialCost());
        cleanProduct.setStockQuantity(product.getStockQuantity());
        cleanProduct.setLowStockThreshold(product.getLowStockThreshold());
        cleanProduct.setCategory(product.getCategory());
        cleanProduct.setStatus(product.getStatus());
        cleanProduct.setVisibility(product.getVisibility());
        cleanProduct.setUser(product.getUser());
        cleanProduct.setCreated(product.getCreated());

        if (product.getTags() != null) {
            cleanProduct.getTags().clear();
            for (var tag : product.getTags()) {
                tag.setProduct(cleanProduct);
                cleanProduct.getTags().add(tag);
            }
        }

        if (product.getImages() != null) {
            cleanProduct.getImages().clear();
            for (var img : product.getImages()) {
                img.setProduct(cleanProduct);
                cleanProduct.getImages().add(img);
            }
        }

        if (product.getMaterials() != null) {
            cleanProduct.getMaterials().clear();
            for (var mat : product.getMaterials()) {
                mat.setProduct(cleanProduct);
                cleanProduct.getMaterials().add(mat);
            }
        }

        if (product.getExtraDetails() != null) {
            cleanProduct.getExtraDetails().clear();
            for (var detail : product.getExtraDetails()) {
                detail.setProduct(cleanProduct);
                cleanProduct.getExtraDetails().add(detail);
            }
        }

        if (product.getComments() != null) {
            cleanProduct.getComments().clear();
            for (var comment : product.getComments()) {
                comment.setProduct(cleanProduct);
                cleanProduct.getComments().add(comment);
            }
        }
        productRepository.save(cleanProduct);
        try {

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("failed");

        }

        return ResponseEntity.ok("Success");
    }


    @GetMapping("/data")
    public ResponseEntity<Product> getItem(){
        Product product = productRepository.findById(3l).orElse(null);

        for(var s : product.getTags()){
            System.out.println(s.getTags());
        }
        System.out.println(product.getProductName());


        return ResponseEntity.ok(product);
    }



}
