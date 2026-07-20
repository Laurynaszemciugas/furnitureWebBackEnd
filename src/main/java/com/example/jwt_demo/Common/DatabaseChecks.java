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

        System.out.println("cheking db stuff");

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

            if (amountOfProductTaken > remainingProduct) {
                throw new ValidationException("Order cannot be filled", Warnings.ERROR);
            }
        }

        for (var prods : order.getProductsData()) {

            Long amountOfProductTaken = prods.getAmountOfProduct();
            Long remainingProduct = prods.getProduct().getStockQuantity();


            if (prods.getProduct().isStockCalculatedManually()) {

                Product userDrivenProduct = prods.getProduct();

                userDrivenProduct.setStockQuantity(Math.abs(amountOfProductTaken - remainingProduct));

                productRepository.save(userDrivenProduct);

                continue;
            } else {
                Product userDrivenProduct = prods.getProduct();

                userDrivenProduct.setStockQuantity(Math.abs(amountOfProductTaken - remainingProduct));

                productRepository.save(userDrivenProduct);
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

    public void checkModifiedOrders(Long orderId, Orders oldOrder) {


        Orders newOrder = orderRepository.findById(orderId).orElseThrow();


        for (var newProduct : newOrder.getProductsData()) {

            Long before = 0L;
            Long after = 0L;

            Long recourcesRequired = 0L;

            OrderProducts oldProducts = oldOrder.getProductsData()
                    .stream()
                    .filter(p -> p.getId().equals(newProduct.getId()))
                    .findFirst()
                    .orElse(null);


            if (oldProducts == null) {
                continue;
            }

            if (oldProducts != null) {

                 before = oldProducts.getAmountOfProduct();
                 after = newProduct.getAmountOfProduct();






            }

            for (var newMaterial : newProduct.getProduct().getMaterials()) {

                ProductMaterials oldMaterial = oldProducts.getProduct().getMaterials()
                        .stream()
                        .filter(p -> p.getId().equals(newMaterial.getId()))
                        .findFirst()
                        .orElse(null);


                if (oldMaterial != null) {

                    recourcesRequired = newMaterial.getAmountUsed();

                    Materials material = materialRepository.findById(newMaterial.getMaterials().getId()).orElseThrow();

                    Long value = before * recourcesRequired - after * recourcesRequired;


                        material.setInStock(material.getInStock() + value);
                        materialRepository.save(material);


                    System.out.println(
                            newMaterial.getMaterials().getMaterialName()
                                    + " OLD: " + before
                                    + " NEW: " + after
                    );


                }


            }
        }
    }
}























