package com.example.jwt_demo.controller;


import com.example.jwt_demo.Common.*;
import com.example.jwt_demo.DTOS.Common.GraphDataLongValue;
import com.example.jwt_demo.DTOS.Common.MiniStatHolder;
import com.example.jwt_demo.DTOS.Common.ReportMiniStatHolder;
import com.example.jwt_demo.DTOS.Material.MaterialInfo;
import com.example.jwt_demo.DTOS.Order.OrderAddProducts;
import com.example.jwt_demo.DTOS.Product.ProductLowStockList;
import com.example.jwt_demo.DTOS.Product.ProductPerformanceReport;
import com.example.jwt_demo.DTOS.Product.ProductReportPieChart;
import com.example.jwt_demo.Entity.*;
import com.example.jwt_demo.Entity.ProductJoin.ProductMaterials;
import com.example.jwt_demo.Enums.*;
import com.example.jwt_demo.DTOS.Product.ProductFeedModel;
import com.example.jwt_demo.FilterDTO.Material.MaterialFilterHolder;
import com.example.jwt_demo.FilterDTO.Prodcut.ProductFilterHolder;
import com.example.jwt_demo.repository.*;
import com.example.jwt_demo.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @Autowired
    Logic logic;

    @Autowired
    ProvidedDataChecker providedDataChecker;

    @Autowired
    DatabaseChecks databaseChecks;

    @Autowired
    ActionTrackerRepository actionTrackerRepository;

    @Autowired
    ProductMaterialRepository productMaterialRepository;

    @Autowired
    ActionMaker actionMaker;

    @Autowired
    ConvertImages convertImages;

    @Autowired
    MultipleProductRepository multipleProductRepository;

    @PostMapping("/saveProduct")
    public ResponseEntity<ErrorResponse> saveProduct(@RequestBody Product product){

        CustomUserDetails user = common.getUserData();



        User currentUser = userRepository.findById(user.getId()).orElseThrow();



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
        cleanProduct.setStockCalculatedManually(product.isStockCalculatedManually());

        if (product.getTags() != null) {
            cleanProduct.getTags().clear();
            for (var tag : product.getTags()) {
                tag.setProduct(cleanProduct);
                tag.setUser(currentUser);
                cleanProduct.getTags().add(tag);
            }
        }



        if (product.getImages() != null) {
            for (var img : product.getImages()) {
                img.setProduct(cleanProduct);
                try {
                    img.setImageUrl(convertImages.saveImage(img.getImageData(), img.getImageUrl()));
                    img.setImageData(null);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
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

        if (product.getSteps() != null) {
            cleanProduct.getSteps().clear();
            for (var steps : product.getSteps()) {
                steps.setProduct(cleanProduct);
                cleanProduct.getSteps().add(steps);
            }
        }

        System.out.println(product.getComments());

        productRepository.save(cleanProduct);

        databaseChecks.calculateProductsStock(user.getId(),false);

        actionMaker.makeAction(String.format("product %s was saved successfully",cleanProduct.getProductName()),user.getId(),null,ActionTrackerEnum.USER, ActionDesciptionEnum.Product_Created);


        return ResponseEntity.ok(new ErrorResponse(String.format("product %s was saved successfully",cleanProduct.getProductName()), Warnings.OK));
    }







    @PostMapping("/addNewMultipleProduct")
    public ResponseEntity<ErrorResponse> saveMultipleProducts(@RequestBody Product product) {


        System.out.println(product);

        CustomUserDetails user = common.getUserData();
        User currentUser = userRepository.findById(user.getId()).orElseThrow();

        Product cleanProduct = new Product();



        // Main product fields
        cleanProduct.setId(null);
        cleanProduct.setProductName(product.getProductName());
        cleanProduct.setSku(product.getSku());
        cleanProduct.setDescription(product.getDescription());
        cleanProduct.setPrice(product.getPrice());
        cleanProduct.setDiscount(product.getDiscount());
        cleanProduct.setMaterialCost(product.getMaterialCost());
        cleanProduct.setStockQuantity(0L);
        cleanProduct.setLowStockThreshold(0L);
        cleanProduct.setCategory(product.getCategory());
        cleanProduct.setStatus(product.getStatus());
        cleanProduct.setVisibility(Visibility.Visible);
        cleanProduct.setCreated(product.getCreated());
        cleanProduct.setStockCalculatedManually(product.isStockCalculatedManually());
        cleanProduct.setUser(currentUser);


        cleanProduct.setMultipleItems(true);

        // Tags belong to the main product
        if (product.getTags() != null) {
            for (var tag : product.getTags()) {
                tag.setProduct(cleanProduct);
                tag.setUser(currentUser);
                cleanProduct.getTags().add(tag);
            }
        }

        // Specifications belong to the main product
        if (product.getExtraDetails() != null) {
            for (var detail : product.getExtraDetails()) {
                detail.setProduct(cleanProduct);
                detail.setUser(currentUser);
                cleanProduct.getExtraDetails().add(detail);
            }
        }

        // Steps to finish belong to the main product
        if (product.getSteps() != null) {
            for (var step : product.getSteps()) {
                step.setProduct(cleanProduct);
                cleanProduct.getSteps().add(step);
            }
        }

        List<ProductMultiple> multiples = new ArrayList<>();
        for(var s : product.getProductMultiples()){

            ProductMultiple prod = new ProductMultiple();
            prod.setProduct(cleanProduct);
            prod.setUser(currentUser);

            // Main product fields
            prod.setId(null);
            prod.setProductName(s.getProductName());
            prod.setSku(s.getSku());
            prod.setDescription(s.getDescription());
            prod.setPrice(s.getPrice());
            prod.setDiscount(s.getDiscount());
            prod.setMaterialCost(s.getMaterialCost());
            prod.setStockQuantity(s.getStockQuantity());
            prod.setLowStockThreshold(s.getLowStockThreshold());
            prod.setCategory(s.getCategory());
            prod.setStatus(s.getStatus());
            prod.setVisibility(s.getVisibility());
            prod.setCreated(s.getCreated());
            prod.setStockCalculatedManually(s.isStockCalculatedManually());
            prod.setUser(currentUser);



            if (s.getImages() != null) {
                for (var img : s.getImages()) {
                    img.setProduct(cleanProduct);
                    try {
                        img.setImageUrl(convertImages.saveImage(img.getImageData(), img.getImageUrl()));
                        img.setImageData(null);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    prod.getImages().add(img);
                }
            }

            if (s.getMaterials() != null) {
                for (var incomingMaterial : s.getMaterials()) {

                    // Create a NEW product-material link.
                    ProductMaterials mats = new ProductMaterials();

                    mats.setNameForRefrence(
                            incomingMaterial.getNameForRefrence());
                    mats.setNewMaterial(incomingMaterial.isNewMaterial());
                    mats.setAmountUsed(incomingMaterial.getAmountUsed());
                    mats.setUnitPrice(incomingMaterial.getUnitPrice());

                    // Resolve the actual inventory material by its own ID.



                        Materials usedMaterial = materialRepository.findByMaterialName(incomingMaterial.getId(), currentUser.getId());




                        mats.setMaterials(usedMaterial);
                        mats.setUnitPrice(usedMaterial.getUnitPrice());

                    // This link belongs to the variant, not the main product.
                    mats.setProduct(null);
                    mats.setProductMultiple(prod);
                    mats.setUser(currentUser);

                    prod.getMaterials().add(mats);
                }
            }

            multiples.add(
                    prod
            );


        }

        cleanProduct.setProductMultiples(multiples);




        productRepository.save(cleanProduct);


        return ResponseEntity.ok(
                new ErrorResponse(
                        "Saved product with multiple product variants",
                        Warnings.OK
                )
        );
    }



    @PostMapping("/saveEditedMultipleProduct")
    @Transactional
    public ResponseEntity<ErrorResponse> saveEdited(
            @RequestBody Product product) {

        CustomUserDetails user = common.getUserData();
        User currentUser = userRepository.findById(user.getId())
                .orElseThrow();

        Product cleanProduct = productRepository.findById(product.getId())
                .orElseThrow(() -> new RuntimeException(
                        "Product not found: " + product.getId()));

        // Update main product fields.
        cleanProduct.setProductName(product.getProductName());
        cleanProduct.setSku(product.getSku());
        cleanProduct.setDescription(product.getDescription());
        cleanProduct.setPrice(product.getPrice());
        cleanProduct.setDiscount(0);
        cleanProduct.setMaterialCost(0);
        cleanProduct.setStockQuantity(0L);
        cleanProduct.setLowStockThreshold(0L);
        cleanProduct.setCategory(product.getCategory());
        cleanProduct.setStatus(product.getStatus());
        cleanProduct.setVisibility(Visibility.Visible);
        cleanProduct.setStockCalculatedManually(false);
        cleanProduct.setUser(currentUser);

        cleanProduct.setMultipleItems(true);



        // Replace main-product tags, without replacing the collection itself.
        if (product.getTags() != null) {
            cleanProduct.getTags().clear();

            for (var incomingTag : product.getTags()) {
                incomingTag.setId(null);
                incomingTag.setProduct(cleanProduct);
                incomingTag.setUser(currentUser);

                cleanProduct.getTags().add(incomingTag);
            }
        }

        // Replace main-product specifications.
        if (product.getExtraDetails() != null) {
            cleanProduct.getExtraDetails().clear();

            for (var incomingDetail : product.getExtraDetails()) {
                incomingDetail.setId(null);
                incomingDetail.setProduct(cleanProduct);
                incomingDetail.setProductMultiple(null);
                incomingDetail.setUser(currentUser);

                cleanProduct.getExtraDetails().add(incomingDetail);
            }
        }

        // Replace main-product finishing steps.
        if (product.getSteps() != null) {
            cleanProduct.getSteps().clear();

            for (var incomingStep : product.getSteps()) {
                incomingStep.setId(null);
                incomingStep.setProduct(cleanProduct);

                cleanProduct.getSteps().add(incomingStep);
            }
        }


// Always replace main-product images
        cleanProduct.getImages().clear();

        if (product.getImages() != null) {
            for (var incomingImage : product.getImages()) {

                ProductImageData image =
                        copyImageForSave(incomingImage, currentUser);

                image.setProduct(cleanProduct);
                image.setProductMultiple(null);

                cleanProduct.getImages().add(image);
            }
        }

        // Rebuild variants using the existing managed collection.
        if (product.getProductMultiples() != null) {


            ProductMultiple variant;


            for (var incomingVariant : product.getProductMultiples()) {

                if(incomingVariant.getId() != null){
                    variant = multipleProductRepository.findById(incomingVariant.getId()).orElseThrow();
                }
                else{
                    variant = new ProductMultiple();
                }

                variant.setProduct(cleanProduct);
                variant.setUser(currentUser);

                variant.setColor(incomingVariant.getColor());
                variant.setProductName(incomingVariant.getProductName());
                variant.setSku(incomingVariant.getSku());
                variant.setDescription(incomingVariant.getDescription());
                variant.setPrice(incomingVariant.getPrice());
                variant.setDiscount(incomingVariant.getDiscount());
                variant.setMaterialCost(incomingVariant.getMaterialCost());
                variant.setStockQuantity(incomingVariant.getStockQuantity());
                variant.setLowStockThreshold(
                        incomingVariant.getLowStockThreshold());
                variant.setCategory(incomingVariant.getCategory());
                variant.setStatus(incomingVariant.getStatus());
                variant.setVisibility(incomingVariant.getVisibility());
                variant.setStock(incomingVariant.getStock());
                variant.setStockCalculatedManually(
                        incomingVariant.isStockCalculatedManually());

                // Always replace variant images
                variant.getImages().clear();

                if (incomingVariant.getImages() != null) {
                    for (var incomingImage : incomingVariant.getImages()) {

                        ProductImageData image =
                                copyImageForSave(incomingImage, currentUser);

                        image.setId(null);
                        image.setProduct(null);
                        image.setProductMultiple(variant);

                        variant.getImages().add(image);
                    }
                }

                // Variant material links.
                if (incomingVariant.getMaterials() != null) {

                    // Remove all existing material links.
                    variant.getMaterials().clear();

                    // Rebuild the collection from the incoming data.
                    for (var incomingMaterial : incomingVariant.getMaterials()) {



                        ProductMaterials materialLink = new ProductMaterials();

                        materialLink.setNameForRefrence(
                                incomingMaterial.getNameForRefrence());

                        materialLink.setNewMaterial(
                                incomingMaterial.isNewMaterial());

                        materialLink.setAmountUsed(
                                incomingMaterial.getAmountUsed());

                        Long id = 0L;

                        try {
                            id = incomingMaterial.getMaterials().getId();
                        } catch (Exception e) {

                            ProductMaterials productMaterials = productMaterialRepository.findById(incomingMaterial.getId()).orElseThrow();

                            id = productMaterials.getMaterials().getId();
                        }
                        Materials usedMaterial = materialRepository
                                .findByMaterialName(
                                        id,
                                        user.getId());



                        materialLink.setMaterials(usedMaterial);
                        materialLink.setUnitPrice(usedMaterial.getUnitPrice());

                        materialLink.setProduct(null);
                        materialLink.setProductMultiple(variant);
                        materialLink.setUser(currentUser);

                        variant.getMaterials().add(materialLink);
                    }

                }


                // Add the variant to the original managed collection.
                cleanProduct.getProductMultiples().add(variant);
            }
        }

        productRepository.save(cleanProduct);

        return ResponseEntity.ok(
                new ErrorResponse(
                        "Saved product with multiple product variants",
                        Warnings.OK
                )
        );
    }

    private ProductImageData copyImageForSave(
            ProductImageData source,
            User currentUser) {

        ProductImageData image = new ProductImageData();

        image.setUuId(source.getUuId());
        image.setImageName(source.getImageName());
        image.setImageType(source.getImageType());
        image.setImageLogic(source.getImageLogic());
        image.setUser(currentUser);

        if (source.getImageData() != null) {
            try {
                image.setImageUrl(
                        convertImages.saveImage(
                                source.getImageData(),
                                source.getImageUrl()
                        )
                );
            } catch (IOException e) {
                throw new RuntimeException(
                        "Failed to save product image", e);
            }
        } else {
            image.setImageUrl(source.getImageUrl());
        }

        image.setImageData(null);

        return image;
    }




    @PostMapping("/getProducts")
    public List<ProductFeedModel> getProducts(@RequestBody ProductFilterHolder productFilterHolder) {

        CustomUserDetails user = common.getUserData();

        productFilterHolder = providedDataChecker.defaultValueChecker(productFilterHolder, ProductFilterHolder.class);



        return productRepository.getAllProducts(
                productFilterHolder.getCategory(),
                productFilterHolder.getStockChoice(),
                productFilterHolder.getVisibility(),
                productFilterHolder.getPrompt(),
                logic.dateConverter(productFilterHolder.getCreatedFrom()),
                logic.dateConverter(productFilterHolder.getCreatedTo()),
                productFilterHolder.getPrice(),
                productFilterHolder.getDiscount(),
                productFilterHolder.getMaterialId(),
                user.getId(),
                PageRequest.of(productFilterHolder.getPage(), productFilterHolder.getPageCount())
        );
    }




    // get product count how many paganation buttons are needed
    @PostMapping("/getPages")
    public Long getProductPages(@RequestBody ProductFilterHolder productFilterHolder) {
        CustomUserDetails user = common.getUserData();

        productFilterHolder = providedDataChecker.defaultValueChecker(productFilterHolder, ProductFilterHolder.class);


        return productRepository.getProductPages(
                productFilterHolder.getCategory(),
                productFilterHolder.getStockChoice(),
                productFilterHolder.getVisibility(),
                productFilterHolder.getPrompt(),
                logic.dateConverter(productFilterHolder.getCreatedFrom()),
                logic.dateConverter(productFilterHolder.getCreatedTo()),
                productFilterHolder.getPrice(),
                productFilterHolder.getDiscount(),
                productFilterHolder.getMaterialId(),
                user.getId()
        );
    }



    // load specific product to edit
    @GetMapping("/getProductToId/{id}")
    public ResponseEntity<Product> getItem(@PathVariable Long id){
        Product product = productRepository.findById(id).orElse(null);

        if(product!=null){

            for(var s : product.getMaterials()){
                System.out.println(s.getAmountUsed());
            }


            return ResponseEntity.ok(product);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }


    @PostMapping("/editProduct")
    public ResponseEntity<ErrorResponse> editProduct(@RequestBody Product product){


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
        existingProduct.setStockCalculatedManually(product.isStockCalculatedManually());


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
                img.setUser(null);
                try {
                    if(img.getImageData() == null) {
                        img.setImageUrl(img.getImageUrl());
                    }
                    else {
                        img.setImageUrl(convertImages.saveImage(img.getImageData(), img.getImageUrl()));
                        img.setImageData(null);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                existingProduct.getImages().add(img);
            }
        }

//        if (product.getImages() != null) {
//            existingProduct.getImages().clear();
//            for (var img : product.getImages()) {
//                img.setProduct(existingProduct);
//                img.setUser(currentUser);
//                existingProduct.getImages().add(img);
//            }
//        }

        if (product.getMaterials() != null) {

            existingProduct.getMaterials().clear();
            for (var mat : product.getMaterials()) {


                Materials usedMaterial = materialRepository.findByMaterialName(mat.getId(), user.getId());

                ProductMaterials newProductMaterial = new ProductMaterials();

                newProductMaterial.setNameForRefrence(mat.getNameForRefrence());
                newProductMaterial.setNewMaterial(mat.isNewMaterial());
                newProductMaterial.setAmountUsed(mat.getAmountUsed());
                newProductMaterial.setUnitPrice(usedMaterial.getUnitPrice());
                newProductMaterial.setMaterials(usedMaterial);
                newProductMaterial.setProduct(existingProduct);
                newProductMaterial.setUser(currentUser);

                existingProduct.getMaterials().add(newProductMaterial);
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

        if (product.getSteps() != null) {
            existingProduct.getSteps().clear();
            for (var steps : product.getSteps()) {
                steps.setProduct(existingProduct);
                existingProduct.getSteps().add(steps);
            }
        }



        productRepository.save(existingProduct);

        databaseChecks.calculateProductsStock(user.getId(),false);

        actionMaker.makeAction(String.format("product %s was modified successfully",product.getProductName()),user.getId(),null,ActionTrackerEnum.USER, ActionDesciptionEnum.Product_Updated);

        return ResponseEntity.ok(new ErrorResponse(String.format("product %s was modified successfully",product.getProductName()),Warnings.OK));

    }

    @PostMapping("/removeProduct")
    public ResponseEntity<ErrorResponse> removeProduct(@RequestBody Long id){

        CustomUserDetails user = common.getUserData();

        Product product = productRepository.findById(id).orElseThrow();

        try {
            productRepository.deleteById(id);
        } catch (Exception e) {
                product.setVisibility(Visibility.NonVisible);
                product.setStatus(Status.Disabled);
                productRepository.save(product);


            actionMaker.makeAction(String.format("product %s was placed to the blacklist successfully",product.getProductName()),user.getId(),null,ActionTrackerEnum.SYSTEM, ActionDesciptionEnum.Product_Status_Change);

            return  ResponseEntity.ok(new ErrorResponse(String.format("Product %s is used it is put into a blackList you cannot use it but you can change that",product.getProductName()),Warnings.OK));
        }



        actionMaker.makeAction(String.format("product %s was removed successfully",product.getProductName()),user.getId(),null,ActionTrackerEnum.USER, ActionDesciptionEnum.Product_Deleted);

        return ResponseEntity.ok(new ErrorResponse("Removed successfully",Warnings.OK));
    }


    @GetMapping("/getProductsForAddOrder")
    public ResponseEntity<List<OrderAddProducts>> getAllProductsForAddNewOrder(){

        CustomUserDetails user = common.getUserData();

        return ResponseEntity.ok(productRepository.getAllProductDataForAddNewOrder(user.getId()));

    }


    @GetMapping("/getExistingData/{id}")
    public ResponseEntity<List<OrderAddProducts>> getAllCurrentProducts(@PathVariable Long id){

        CustomUserDetails user = common.getUserData();

        System.out.println(id);

        System.out.println("getting data ");
        List<OrderAddProducts> list = productRepository.getExistingDataForOrder(id,user.getId());

        for(var s : list){
            System.out.println(s.getId());
        }

        return ResponseEntity.ok(productRepository.getExistingDataForOrder(id, user.getId()));

    }

    @GetMapping("/getProductMiniStats/{from}/{to}")
    public ResponseEntity<MiniStatHolder> getProductMiniStats(@PathVariable LocalDate from, @PathVariable LocalDate to){
        CustomUserDetails user = common.getUserData();

        return ResponseEntity.ok(productRepository.getProductMiniStats(logic.dateConverter(from),logic.dateConverter(to), user.getId()));
    }

    // report page calls

    @GetMapping("/getMiniStats/{fromDate}/{toDate}")
    public ResponseEntity<ReportMiniStatHolder> getMiniStats(
            @PathVariable LocalDate fromDate,
            @PathVariable LocalDate toDate) {

        CustomUserDetails user = common.getUserData();

        LocalDate preFrom = fromDate.withDayOfMonth(1).minusMonths(1);
        LocalDate preTo = preFrom.plusMonths(1).minusDays(1);



        Object[] data = productRepository.getProductReportMiniStats(
                logic.dateConverter(fromDate),
                logic.dateConverter(toDate),
                logic.dateConverter(preFrom),
                logic.dateConverter(preTo),
                user.getId()
        ).get(0);


        ReportMiniStatHolder holder = new ReportMiniStatHolder(
                data[0],
                data[1],
                data[2],
                data[3],
                data[4],
                data[5],
                data[6],
                data[7]
        );

        return ResponseEntity.ok(holder);
    }

    @GetMapping("/getTopProducts/{from}/{to}/{amountOfData}")
    public ResponseEntity<List<GraphDataLongValue>> getTopProducts(@PathVariable LocalDate from, @PathVariable LocalDate to, @PathVariable int amountOfData){

        CustomUserDetails user = common.getUserData();

        return ResponseEntity.ok(productRepository.getTopSellingProducts(logic.dateConverter(from),logic.dateConverter(to),PageRequest.of(0,amountOfData),user.getId()));
    }

    @GetMapping("/getPieChartProductReport/{from}/{to}")
    public ResponseEntity<List<ProductReportPieChart>> getPieChartProductReport(@PathVariable LocalDate from, @PathVariable LocalDate to){

        CustomUserDetails user = common.getUserData();

        return ResponseEntity.ok(productRepository.getProductByCategory(logic.dateConverter(from),logic.dateConverter(to), user.getId()));
    }

    @GetMapping("/getLowStockAlerts/{from}/{to}")
    public ResponseEntity<List<ProductLowStockList>> getLowStockAlerts(@PathVariable LocalDate from, @PathVariable LocalDate to){

        CustomUserDetails user = common.getUserData();

        return ResponseEntity.ok(productRepository.getProductLowList(logic.dateConverter(from),logic.dateConverter(to), user.getId()));
    }

    @GetMapping("/getProductPerformance/{from}/{to}")
    public ResponseEntity<List<ProductPerformanceReport>> getProductPerformanceReport(@PathVariable LocalDate from, @PathVariable LocalDate to){

        CustomUserDetails user = common.getUserData();

        return ResponseEntity.ok(productRepository.getProductPerformance(logic.dateConverter(from),logic.dateConverter(to), user.getId()));
    }






}
