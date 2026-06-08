package com.example.jwt_demo.controller;


import com.example.jwt_demo.DTOS.Order.OrderAddProducts;
import com.example.jwt_demo.Entity.Materials;
import com.example.jwt_demo.Entity.Product;
import com.example.jwt_demo.Entity.ProductJoin.ProductMaterials;
import com.example.jwt_demo.Entity.User;
import com.example.jwt_demo.Enums.Category;
import com.example.jwt_demo.Enums.Status;
import com.example.jwt_demo.Enums.Stock;
import com.example.jwt_demo.Enums.Visibility;
import com.example.jwt_demo.FrontEndModels.ProductFeedModel;
import com.example.jwt_demo.repository.MaterialRepository;
import com.example.jwt_demo.repository.ProductRepository;
import com.example.jwt_demo.repository.UserRepository;
import com.example.jwt_demo.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    MaterialRepository materialRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    Common common;



    @PostMapping("/saveProduct")
    public ResponseEntity<String> saveProduct(@RequestBody Product product){

        CustomUserDetails user = common.getUserData();

        User currentUser = userRepository.findById(user.getId()).orElseThrow();


        System.out.println(user.getUsername());
        System.out.println(user.getId());


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
        cleanProduct.setUser(currentUser);
        cleanProduct.setStock(Stock.No_Stock);

        if (product.getTags() != null) {
            cleanProduct.getTags().clear();
            for (var tag : product.getTags()) {
                tag.setProduct(cleanProduct);
                tag.setUser(currentUser);
                cleanProduct.getTags().add(tag);
            }
        }

        if (product.getImages() != null) {
            cleanProduct.getImages().clear();
            for (var img : product.getImages()) {
                img.setProduct(cleanProduct);
                img.setUser(currentUser);
                cleanProduct.getImages().add(img);
            }
        }

        if (product.getMaterials() != null) {
            cleanProduct.getMaterials().clear();
            for (var mat : product.getMaterials()) {

                ProductMaterials newMat = new ProductMaterials();

                Materials usedMaterial =
                        materialRepository.findByMaterialName(mat.getId(), user.getId());

                newMat.setMaterials(usedMaterial);
                newMat.setUnitPrice(usedMaterial.getUnitPrice());
                newMat.setAmountUsed(mat.getAmountUsed());

                newMat.setProduct(cleanProduct);
                newMat.setUser(currentUser);
                cleanProduct.getMaterials().add(newMat);
            }
        }

        if (product.getExtraDetails() != null) {
            cleanProduct.getExtraDetails().clear();
            for (var detail : product.getExtraDetails()) {
                detail.setProduct(cleanProduct);
                detail.setUser(currentUser);
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


    @GetMapping("/getProducts/{stock}/{category}/{prompt}/{visibility}/{page}/{size}")
    public List<ProductFeedModel> getProducts(
            @PathVariable Stock stock,
            @PathVariable Category category,
            @PathVariable String prompt,
            @PathVariable Visibility visibility,
            @PathVariable int page,
            @PathVariable int size
    ) {

        CustomUserDetails user = common.getUserData();

        return productRepository.getAllProducts(category,stock,prompt,visibility,user.getId(),PageRequest.of(page, size));
    }



    // get product count how many paganation buttons are needed
    @GetMapping("/getPages/{stock}/{category}/{prompt}/{visibility}")
    public Long getProductPages(
            @PathVariable Stock stock,
            @PathVariable Category category,
            @PathVariable String prompt,
            @PathVariable Visibility visibility
    ) {

        CustomUserDetails user = common.getUserData();
        return productRepository.getProductPages(category,stock,prompt,visibility,user.getId());
    }



    // load specific product to edit
    @GetMapping("/getProductToId/{id}")
    public ResponseEntity<Product> getItem(@PathVariable Long id){
        Product product = productRepository.findById(id).orElse(null);

        if(product!=null){
            System.out.println("found");
            return ResponseEntity.ok(product);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }


    @PostMapping("/editProduct")
    public ResponseEntity<String> editProduct(@RequestBody Product product){


        CustomUserDetails user = common.getUserData();

        Product existingProduct = productRepository.findById(product.getId()).orElseThrow();

        User currentUser = userRepository.findById(user.getId()).orElseThrow();

        existingProduct.setProductName(product.getProductName());
        existingProduct.setSku(product.getSku());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setDiscount(product.getDiscount());
        existingProduct.setMaterialCost(product.getMaterialCost());
        existingProduct.setStockQuantity(product.getStockQuantity());
        existingProduct.setLowStockThreshold(product.getLowStockThreshold());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setStatus(product.getStatus());
        existingProduct.setVisibility(product.getVisibility());
        existingProduct.setUser(product.getUser());
        existingProduct.setCreated(product.getCreated());
        existingProduct.setUser(currentUser);


        // stock check later
        existingProduct.setStock(Stock.No_Stock);

        if (product.getTags() != null) {
            existingProduct.getTags().clear();
            for (var tag : product.getTags()) {
                tag.setProduct(existingProduct);
                tag.setUser(currentUser);
                existingProduct.getTags().add(tag);
            }
        }

        if (product.getImages() != null) {
            existingProduct.getImages().clear();
            for (var img : product.getImages()) {
                img.setProduct(existingProduct);
                img.setUser(currentUser);
                existingProduct.getImages().add(img);
            }
        }

        if (product.getMaterials() != null) {
            existingProduct.getMaterials().clear();
            for (var mat : product.getMaterials()) {

                Materials usedMaterial = materialRepository.findByMaterialName(mat.getId(),user.getId());

                mat.setMaterials(usedMaterial);
                mat.setUnitPrice(usedMaterial.getUnitPrice());

                mat.setProduct(existingProduct);
                mat.setUser(currentUser);

                existingProduct.getMaterials().add(mat);
            }
        }

        if (product.getExtraDetails() != null) {
            existingProduct.getExtraDetails().clear();
            for (var detail : product.getExtraDetails()) {
                detail.setProduct(existingProduct);
                detail.setUser(currentUser);
                existingProduct.getExtraDetails().add(detail);
            }
        }

        if (product.getComments() != null) {
            existingProduct.getComments().clear();
            for (var comment : product.getComments()) {
                comment.setProduct(existingProduct);
                existingProduct.getComments().add(comment);
            }
        }


        productRepository.save(existingProduct);

        System.out.println(user.getUsername());

        System.out.println(product.getProductName());

        return ResponseEntity.ok("got data");

    }

    @PostMapping("/removeProduct")
    public ResponseEntity<String> removeProduct(@RequestBody Long id){

        try {
            productRepository.deleteById(id);
        } catch (Exception e) {
            Product product = productRepository.findById(id).orElseThrow();
                product.setVisibility(Visibility.NonVisible);
                product.setStatus(Status.Disabled);
                productRepository.save(product);
            return  ResponseEntity.ok("Product is used it is put into a blackList you cannot use it but you can change that");
        }
        return ResponseEntity.ok("Removed successfully");
    }


    @GetMapping("/getProductsForAddOrder")
    public ResponseEntity<List<OrderAddProducts>> getAllProductsForAddNewOrder(){

        return ResponseEntity.ok(productRepository.getAllProductDataForAddNewOrder());

    }


    @GetMapping("/getExistingData/{id}")
    public ResponseEntity<List<OrderAddProducts>> getAllCurrentProducts(@PathVariable Long id){

        System.out.println(id);

        System.out.println("getting data ");
        List<OrderAddProducts> list = productRepository.getExistingDataForOrder(id);

        for(var s : list){
            System.out.println(s.getId());
        }

        return ResponseEntity.ok(productRepository.getExistingDataForOrder(id));

    }


}
