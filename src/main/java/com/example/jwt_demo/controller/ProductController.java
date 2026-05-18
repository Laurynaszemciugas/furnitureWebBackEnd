package com.example.jwt_demo.controller;


import com.example.jwt_demo.Entity.Product;
import com.example.jwt_demo.Entity.User;
import com.example.jwt_demo.Enums.Category;
import com.example.jwt_demo.Enums.Stock;
import com.example.jwt_demo.FrontEndModels.ProductFeedModel;
import com.example.jwt_demo.repository.ProductRepository;
import com.example.jwt_demo.repository.UserRepository;
import com.example.jwt_demo.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    UserRepository userRepository;

    @PostMapping("/saveProduct")
    public ResponseEntity<String> saveProduct(@RequestBody Product product){

        CustomUserDetails user =
                (CustomUserDetails) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        Optional<User> currentUser = userRepository.findById(user.getId());


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
        cleanProduct.setUser(currentUser.get());
        cleanProduct.setStock(Stock.No_Stock);

        if (product.getTags() != null) {
            cleanProduct.getTags().clear();
            for (var tag : product.getTags()) {
                tag.setProduct(cleanProduct);
                tag.setUser(currentUser.get());
                cleanProduct.getTags().add(tag);
            }
        }

        if (product.getImages() != null) {
            cleanProduct.getImages().clear();
            for (var img : product.getImages()) {
                img.setProduct(cleanProduct);
                img.setUser(currentUser.get());
                cleanProduct.getImages().add(img);
            }
        }

        if (product.getMaterials() != null) {
            cleanProduct.getMaterials().clear();
            for (var mat : product.getMaterials()) {
                mat.setProduct(cleanProduct);
                mat.setUser(currentUser.get());
                cleanProduct.getMaterials().add(mat);
            }
        }

        if (product.getExtraDetails() != null) {
            cleanProduct.getExtraDetails().clear();
            for (var detail : product.getExtraDetails()) {
                detail.setProduct(cleanProduct);
                detail.setUser(currentUser.get());
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


    @GetMapping("/getProducts/{stock}/{category}/{prompt}/{page}/{size}")
    public List<ProductFeedModel> getProducts(
            @PathVariable Stock stock,
            @PathVariable Category category,
            @PathVariable String prompt,
            @PathVariable int page,
            @PathVariable int size
    ) {

        return productRepository.getAllProducts(category,stock,prompt,PageRequest.of(page, size));
    }



    // get product count how many paganation buttons are needed
    @GetMapping("/getProductsPageCount")
    public Long getProductPages() {
        // make so user JWT is extracted the id
        return productRepository.getProductPages(1l);
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

        CustomUserDetails user =
                (CustomUserDetails) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

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




}
