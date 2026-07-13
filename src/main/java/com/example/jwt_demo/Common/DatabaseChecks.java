package com.example.jwt_demo.Common;

import com.example.jwt_demo.Entity.Product;
import com.example.jwt_demo.Entity.User;
import com.example.jwt_demo.Enums.Stock;
import com.example.jwt_demo.repository.ProductRepository;
import com.example.jwt_demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DatabaseChecks {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ProductRepository productRepository;


    public void calculateProductsStock(Long userId){

        System.out.println("cheking db stuff");

        List<User> user = userRepository.findAll();

//        if(userId !=null) {
//             user = userRepository.findAll();
//        }
//
//        else{
//            User singleUser = userRepository.findById(userId).orElseThrow();
//
//            user.add(singleUser);
//
//        }



        for(var s : user){



            List<Product> product = productRepository.getProductsAccordingToUserId(s.getId());

            for(var prods : product){

                String limitingMaterial = null;
                Long lowestAmountToMake = null;

                if(prods.isStockCalculatedManually()){
                    continue;
                }


                for(var matStats : prods.getMaterials()) {


                    Long amountUsed = matStats.getAmountUsed();
                    Long materialStock = matStats.getMaterials().getInStock();
                    String materialName = matStats.getMaterials().getMaterialName();

                    Long canProduce = materialStock / amountUsed;

                    if(limitingMaterial == null && lowestAmountToMake == null) {
                        lowestAmountToMake = canProduce;
                        limitingMaterial = materialName;
                    }

                    if(canProduce <= lowestAmountToMake){
                        lowestAmountToMake = canProduce;
                        limitingMaterial = materialName;
                    }

                }







                Long lowThreshold = prods.getLowStockThreshold();

                if(lowestAmountToMake > lowThreshold){
                    prods.setStock(Stock.In_Stock);
                }
                if(lowestAmountToMake <= lowThreshold){
                    prods.setStock(Stock.Low_Stock);
                }
                if(lowestAmountToMake == 0){
                    prods.setStock(Stock.No_Stock);
                }

                prods.setStockQuantity(lowestAmountToMake);

                productRepository.save(prods);









            }


        }


    }



}
