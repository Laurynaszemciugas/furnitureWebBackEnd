package com.example.jwt_demo.Common;

import com.example.jwt_demo.Entity.Materials;
import com.example.jwt_demo.Entity.OrderJoin.OrderProducts;
import com.example.jwt_demo.Entity.Orders;
import com.example.jwt_demo.Entity.Product;
import com.example.jwt_demo.Entity.ProductJoin.ProductMaterials;
import com.example.jwt_demo.Entity.User;
import com.example.jwt_demo.Enums.Stock;
import com.example.jwt_demo.Enums.Warnings;
import com.example.jwt_demo.GlobalExseptions.Exseptions.ValidationException;
import com.example.jwt_demo.repository.MaterialRepository;
import com.example.jwt_demo.repository.OrderRepository;
import com.example.jwt_demo.repository.ProductRepository;
import com.example.jwt_demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static com.fasterxml.jackson.databind.type.LogicalType.Map;

@Service
public class DatabaseChecks {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    MaterialRepository materialRepository;

    @Autowired
    Logic logic;


    public void calculateProductsStock(Long userId, boolean changeMaterialSupply) {

        List<User> user = userRepository.findAll();

        Long stockWas = 0L;
        Long stockNew = 0L;

        if (userId == null) {
            user = userRepository.findAll();
        } else {
            User singleUser = userRepository.findById(userId).orElseThrow();

            user.add(singleUser);

        }


        for (var s : user) {


            List<Product> product = productRepository.getProductsAccordingToUserId(s.getId());

            for (var prods : product) {

                String limitingMaterial = null;
                Long lowestAmountToMake = null;

                if (prods.isStockCalculatedManually()) {
                    continue;
                }


                for (var matStats : prods.getMaterials()) {

                    if (prods.getMaterials().isEmpty()) {
                        continue;
                    }


                    Long amountUsed = matStats.getAmountUsed();
                    Long materialStock = matStats.getMaterials().getInStock();
                    String materialName = matStats.getMaterials().getMaterialName();

                    Long canProduce = materialStock / amountUsed;

                    stockWas = matStats.getMaterials().getInStock();
                    stockNew = Math.abs(stockWas - amountUsed);


                    if (changeMaterialSupply) {
                        logic.materialMovementTracker(userId, matStats.getMaterials(), stockWas, stockNew);
                    }


                    if (limitingMaterial == null && lowestAmountToMake == null) {
                        lowestAmountToMake = canProduce;
                        limitingMaterial = materialName;
                    }

                    if (canProduce <= lowestAmountToMake) {
                        lowestAmountToMake = canProduce;
                        limitingMaterial = materialName;
                    }

                }


                Long lowThreshold = prods.getLowStockThreshold();

                if (lowestAmountToMake > lowThreshold) {
                    prods.setStock(Stock.In_Stock);
                }
                if (lowestAmountToMake <= lowThreshold) {
                    prods.setStock(Stock.Low_Stock);
                }
                if (lowestAmountToMake == 0) {
                    prods.setStock(Stock.No_Stock);
                }

                prods.setStockQuantity(lowestAmountToMake);

                productRepository.save(prods);


            }


        }


    }


    public void calculateMaterialsStock(Long orderId) {

        System.out.println("cheking db stuff");

        Orders order = orderRepository.findById(orderId).orElseThrow();




        for (var prods : order.getProductsData()) {

            Long amountOfProductTaken = prods.getAmountOfProduct();
            Long remainingProduct = prods.getProduct().getStockQuantity();

// seems useless i have product update btw
            Product userDrivenProduct = prods.getProduct();

            userDrivenProduct.setStockQuantity(
                    Math.abs(amountOfProductTaken - remainingProduct)
            );

            productRepository.save(userDrivenProduct);


            if (prods.getProduct().isStockCalculatedManually()) {
                continue;
            }


            for (var mats : prods.getProduct().getMaterials()) {

                Materials mat = materialRepository.findById(mats.getMaterials().getId()).orElseThrow();

                Long materialUsed = mats.getAmountUsed();

                Long getStock = mat.getInStock();


                Long newStock = getStock - (materialUsed * amountOfProductTaken);

                if (newStock < 0) {
                    newStock = 0L;
                }

                mat.setInStock(newStock);

                Long lowThreshold = mat.getMinThresHold();

                if (newStock > lowThreshold) {
                    mat.setStock(Stock.In_Stock);
                }
                if (newStock <= lowThreshold) {
                    mat.setStock(Stock.Low_Stock);
                }
                if (newStock == 0) {
                    mat.setStock(Stock.No_Stock);
                }

                materialRepository.save(mat);


            }


        }


    }


    public void checkIfOrderPossible(Long orderId, Orders oldOrder){

        Orders order = orderRepository.findById(orderId).orElseThrow();

        for (var prods : order.getProductsData()) {

            if (prods.getProduct().isStockCalculatedManually()) {
                continue;
            }

            Long amountOfProductTaken = prods.getAmountOfProduct();
            Long remainingProduct = prods.getProduct().getStockQuantity();

            OrderProducts old = oldOrder.getProductsData().stream().filter(p->p.getProduct().getId().equals(prods.getProduct().getId())).findFirst().orElse(null);

            if(old.getAmountOfProduct() != null) {

                amountOfProductTaken = amountOfProductTaken - old.getAmountOfProduct();
            }

            if (amountOfProductTaken > remainingProduct) {
                throw new ValidationException("Order cannot be filled", Warnings.ERROR);
            }
        }
    }

    public void checkModifiedOrders(Long orderId, Orders oldOrder) {


        Orders newOrder = orderRepository.findById(orderId).orElseThrow();

        // check if order has new items
        for(var productNew : newOrder.getProductsData()) {

            OrderProducts productOld = oldOrder.getProductsData()
                    .stream()
                    .filter(p -> p.getProduct().getId().equals(productNew.getProduct().getId()))
                    .findFirst()
                    .orElse(null);

            if (productOld == null) {
                System.out.println("found added material");

                if (productNew.getProduct().isStockCalculatedManually()) {

                    Product manuallySetProduct = productRepository.findById(productNew.getProduct().getId()).orElseThrow();

                    Long productStock = manuallySetProduct.getStockQuantity();
                    Long takenProductCount = productNew.getAmountOfProduct();

                    Long stock = productStock - takenProductCount;

                    manuallySetProduct.setStockQuantity(stock);

                    productRepository.save(manuallySetProduct);

                    continue;
                }


                for (var material : productNew.getProduct().getMaterials()) {


                    Materials newlyAddedProductsMaterial = materialRepository.findById(material.getMaterials().getId()).orElseThrow();
                    Long materialStock = newlyAddedProductsMaterial.getInStock();
                    Long takenProductCount = productNew.getAmountOfProduct();
                    Long amountMaterialNeededForOneProduct = material.getAmountUsed();

                    Long stock = materialStock - (takenProductCount * amountMaterialNeededForOneProduct);

                    newlyAddedProductsMaterial.setInStock(stock);

                    materialRepository.save(newlyAddedProductsMaterial);


                }


            }
        }

            // check if material was removed
            for(var productOld : oldOrder.getProductsData()) {

                OrderProducts productNew = newOrder.getProductsData()
                        .stream()
                        .filter(p -> p.getProduct().getId().equals(productOld.getProduct().getId()))
                        .findFirst()
                        .orElse(null);

                if (productNew == null) {

                    System.out.println("found removed material");

                    if (productOld.getProduct().isStockCalculatedManually()) {

                        Product manuallySetProduct = productRepository.findById(productOld.getProduct().getId()).orElseThrow();

                        Long productStock = manuallySetProduct.getStockQuantity();
                        Long takenProductCount = productOld.getAmountOfProduct();

                        Long stock = productStock + takenProductCount;

                        manuallySetProduct.setStockQuantity(stock);

                        productRepository.save(manuallySetProduct);

                        continue;
                    }


                    for (var material : productOld.getProduct().getMaterials()) {


                        Materials newlyAddedProductsMaterial = materialRepository.findById(material.getMaterials().getId()).orElseThrow();
                        Long materialStock = newlyAddedProductsMaterial.getInStock();
                        Long takenProductCount = productOld.getAmountOfProduct();
                        Long amountMaterialNeededForOneProduct = material.getAmountUsed();

                        Long stock = materialStock + (takenProductCount * amountMaterialNeededForOneProduct);

                        newlyAddedProductsMaterial.setInStock(stock);

                        materialRepository.save(newlyAddedProductsMaterial);


                    }


                }

            }

            // check if order was modified like value was set from 5 to 10
                for(var productNew : newOrder.getProductsData()) {



                    OrderProducts productOld = oldOrder.getProductsData()
                            .stream()
                            .filter(p -> p.getProduct().getId().equals(productNew.getProduct().getId()))
                            .findFirst()
                            .orElse(null);


                    Long oldAmountTaken = 0L;

                    if(productOld == null){
                        System.out.println("Product is new but it was fixed my other method");
                        continue;
                    }

                    oldAmountTaken = productOld.getAmountOfProduct();


                    if(productNew.getAmountOfProduct().equals(oldAmountTaken)){
                        System.out.println("found product which was not modified skiped");
                        continue;
                    }


                    if(productNew.getProduct().isStockCalculatedManually()){

                        Long newAmountTaken = productNew.getAmountOfProduct();


                        Product manuallySetProduct = productRepository.findById(productOld.getProduct().getId()).orElseThrow();
                        Long productStock = manuallySetProduct.getStockQuantity();


                        Long stock = productStock + (oldAmountTaken - newAmountTaken);

                        manuallySetProduct.setStockQuantity(stock);

                        productRepository.save(manuallySetProduct);


                        continue;
                    }



                    for(var material : productNew.getProduct().getMaterials()) {

                        Long newAmountTaken = productNew.getAmountOfProduct();


                        Materials newlyAddedProductsMaterial = materialRepository.findById(material.getMaterials().getId()).orElseThrow();
                        Long materialStock = newlyAddedProductsMaterial.getInStock();
                        Long amountMaterialNeededForOneProduct = material.getAmountUsed();

                        Long amountTakenDifference = oldAmountTaken - newAmountTaken;

                        Long stock = materialStock + (amountTakenDifference * amountMaterialNeededForOneProduct);

                        newlyAddedProductsMaterial.setInStock(stock);

                        materialRepository.save(newlyAddedProductsMaterial);


                    }








                }




        }




//        for (var oldProduct : oldOrder.getProductsData()) {
//
//
//
//            OrderProducts newProduct = newOrder.getProductsData()
//                    .stream()
//                    .filter(p -> p.getProduct().getId()
//                            .equals(oldProduct.getProduct().getId()))
//                    .findFirst()
//                    .orElse(null);
//
//
//
//            if (newProduct == null) {
//
//
//
//                for (var oldMaterial : oldProduct.getProduct().getMaterials()) {
//
//                    if (oldProduct.getProduct().isStockCalculatedManually()) {
//
//                        Long stockWas = oldProduct.getAmountOfProduct();
//
//                        Product getManuallySetProduct = productRepository.findById(oldProduct.getProduct().getId()).orElseThrow();
//
//                        getManuallySetProduct.setStockQuantity(getManuallySetProduct.getStockQuantity() + stockWas);
//
//                        productRepository.save(getManuallySetProduct);
//
//
//                        continue;
//                    }
//
//
//
//                    Materials materials = materialRepository.findById(oldMaterial.getMaterials().getId()).orElseThrow();
//
//                    Long returnedResources = oldMaterial.getAmountUsed() * oldProduct.getAmountOfProduct();
//
//                    materials.setInStock(materials.getInStock() + returnedResources);
//
//                    materialRepository.save(materials);
//
//
//                }
//                }
//
//            }
//
//
//
//
//
//
//        for (var newProduct : newOrder.getProductsData()) {
//
//            if (newProduct.getProduct().isStockCalculatedManually()) {
//                continue;
//            }
//
//
//            Long before = 0L;
//            Long after = 0L;
//
//            Long recourcesRequired = 0L;
//
//
//
//            OrderProducts oldProducts = oldOrder.getProductsData()
//                    .stream()
//                    .filter(p -> p.getProduct().getId().equals(newProduct.getProduct().getId()))
//                    .findFirst()
//                    .orElse(null);
//
//
////            boolean exists = oldOrder.getProductsData()
////                    .stream()
////                    .anyMatch(p-> p.getProduct().getId().equals(newProduct.getProduct().getId()));
//
//
//
//
//
//            if (oldProducts != null) {
//
//                before = oldProducts.getAmountOfProduct();
//                after = newProduct.getAmountOfProduct();
//
//
//            }
//
//            // !exists
//            if (oldProducts == null) {
//
//
//                if (newProduct.getProduct().isStockCalculatedManually()) {
//                    continue;
//                }
//
//                Long newRecource = 0L;
//                Long countOfProduct = 0L;
//
//                Materials newlyAddedMaterial = materialRepository.findById(newProduct.getProduct().getId()).orElseThrow();
//
//                    newRecource = newProduct.getProduct().getMaterials()
//                            .stream()
//                            .filter(p->p.getId().equals(newlyAddedMaterial.getId()))
//                            .map(ProductMaterials::getAmountUsed)
//                            .findFirst()
//                            .orElse(0L);
//
//                countOfProduct = newProduct.getAmountOfProduct();
//
//                newlyAddedMaterial.setInStock(newlyAddedMaterial.getInStock() - (newRecource*countOfProduct) );
//
//
//                materialRepository.save(newlyAddedMaterial);
//
//            }
//
//
//            if (oldProducts != null && oldProducts.getProduct().getMaterials() != null) {
//            for (var newMaterial : newProduct.getProduct().getMaterials()) {
//
//                ProductMaterials oldMaterial = oldProducts.getProduct().getMaterials()
//                        .stream()
//                        .filter(p -> p.getMaterials().getId().equals(newMaterial.getMaterials().getId()))
//                        .findFirst()
//                        .orElse(null);
//
//
//                if (oldMaterial != null) {
//
//                    recourcesRequired = newMaterial.getAmountUsed();
//
//                    Materials material = materialRepository.findById(newMaterial.getMaterials().getId()).orElseThrow();
//
//                    Long value = before * recourcesRequired - after * recourcesRequired;
//
//
//                    material.setInStock(material.getInStock() + value);
//                    materialRepository.save(material);
//
//
//                    System.out.println(
//                            newMaterial.getMaterials().getMaterialName()
//                                    + " OLD: " + before
//                                    + " NEW: " + after
//                    );
//
//
//                }
//            }
//
//            }
//        }
    }























