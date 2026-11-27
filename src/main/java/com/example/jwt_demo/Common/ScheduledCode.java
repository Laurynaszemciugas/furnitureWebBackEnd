package com.example.jwt_demo.Common;

import com.example.jwt_demo.Common.InternetScraper.PriceResult;
import com.example.jwt_demo.Common.InternetScraper.Scraper;
import com.example.jwt_demo.Common.OrderAcceptance.OrderAcceptingModels;
import com.example.jwt_demo.Entity.*;
import com.example.jwt_demo.Entity.ProductJoin.ProductMaterials;
import com.example.jwt_demo.Enums.NotificationTypes;
import com.example.jwt_demo.Enums.OrderProcessing;
import com.example.jwt_demo.repository.*;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class ScheduledCode {


    @Autowired
    UserRepository userRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    MaterialRepository materialRepository;

    @Autowired
    OrderAcceptingModels orderAcceptingModels;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    MultipleProductRepository multipleProductRepository;

    @Autowired
    ProductPriceTrackerRepository productPriceTrackerRepository;

    @Autowired
    Scraper scraper;

    @Autowired
    Logic logic;

    // ===============================================================
    // Product cost tracker
    // ===============================================================

    @Scheduled(cron = "0 */30 * * * MON-FRI")
    @Transactional
    public void productPriceTracker(){

        System.out.println("baaaa");

        List<Product> products = productRepository.findAll();

        if(!products.isEmpty()){

            User user = products.get(0).getUser();
            logic.addNewNotification("No_picture.png",
                    "Product price tracker",
                    "Products prices was saved to the memory",null,
                    NotificationTypes.SYSTEM,
                    null,
                    null,
                    user.getId());

        for(var s : products){



            if(s.getMultipleItems()){
                for(var multi : s.getProductMultiples()){
                    ProductPriceTracker productPriceTracker = new ProductPriceTracker();

                    productPriceTracker.setProduct(s);
                    productPriceTracker.setPrice(multi.getPrice());
                    productPriceTracker.setProductMultiple(multi);

                    productPriceTrackerRepository.save(productPriceTracker);
                }
            }
            else{
                ProductPriceTracker productPriceTracker = new ProductPriceTracker();

                productPriceTracker.setProduct(s);
                productPriceTracker.setPrice(s.getPrice());
                productPriceTracker.setProductMultiple(null);

                productPriceTrackerRepository.save(productPriceTracker);

            }


        }
        }


    }


    // ===============================================================
    // material cost traker and checker
    // ===============================================================
    @SneakyThrows
    @Scheduled(cron = "0 */1 * * * MON-FRI")
    @Transactional
    public void materialCostTrakerAndChanger(){

        List<Materials> materials = materialRepository.findAll();

        if(!materials.isEmpty()){

            for(var s :materials) {

                if(s.getMaterialUrl() == null){
                    System.out.println("Found null url skip");
                    continue;
                }
                List<PriceResult> items = scraper.scraper(s.getMaterialUrl());
                // sort so the cheapest is at the top
                items.sort(Comparator.comparingDouble(PriceResult::getPrice));

                // check if the current price is cheaper or higher than the DB new

                PriceResult materialFromInternet = items.get(0); // get the cheapest one hopefully its always right one because user chosen the right one

                Double dbPrice = s.getUnitPrice();
                Double internetPrice = materialFromInternet.getPrice();

                if(dbPrice.equals(internetPrice)){
                    continue;
                }
                else{

                    logic.addNewNotification(logic.extractMainImage(null,s.getImages()),
                            String.format("Material %s [%d] price is changed",s.getMaterialName(),s.getId()),
                            String.format("Material old price - [%.2f] ",s.getUnitPrice()),String.format("new material price [%.2f]",internetPrice),
                            NotificationTypes.PRICE,
                            s.getId(),
                            null,
                            s.getUser().getId());

                    Materials mat = materialRepository.findById(s.getId()).orElseThrow();
                    mat.setUnitPrice(internetPrice);
                    materialRepository.save(mat);


                    changeProductMaterialCosts(mat.getId());

                }



            }

        }



    }


    public void changeProductMaterialCosts(Long materialId){


        List<Long> normalProducts = productRepository.idsOfNormalProductComparedToMaterial(materialId);

        List<Long> multiProducts = productRepository.idsOfMultiProductComparedToMaterial(materialId);

        System.out.println(normalProducts.isEmpty());
        System.out.println(multiProducts.isEmpty());


        if(!normalProducts.isEmpty()){

            for(var s : normalProducts){
                Product product = productRepository.findById(s).orElseThrow();
                Materials material = materialRepository.findById(materialId).orElseThrow();

                Double totalMaterialCost = 0.0;

                for(var mats : product.getMaterials()){

                    if(mats.getMaterials().equals(material)) {
                        mats.setUnitPrice(material.getUnitPrice());
                    }
                }

                for(var mats : product.getMaterials()){
                    totalMaterialCost += mats.getUnitPrice();
                }

                product.setMaterialCost(totalMaterialCost);

                productRepository.save(product);

                logic.addNewNotification(logic.extractMainImage(product.getImages(),null),
                        String.format("Normal product %s material price have changed",product.getProductName()),
                        "Product material cost have changed the actual cost didn't please check the price and change the price of the actual product if necessary",null,
                        NotificationTypes.PRICE,
                        s,
                        null,
                        material.getUser().getId());

            }


        }

        if(!multiProducts.isEmpty()){

            for(var s : multiProducts){
                ProductMultiple product = multipleProductRepository.findById(s).orElseThrow();
                Materials material = materialRepository.findById(materialId).orElseThrow();


                Double totalMaterialCost = 0.0;

                for(var mats : product.getMaterials()){
                    if(mats.getMaterials().equals(material)) {
                        mats.setUnitPrice(material.getUnitPrice());
                    }
                }


                for(var mats : product.getMaterials()){
                    totalMaterialCost += mats.getUnitPrice();
                }

                product.setMaterialCost(totalMaterialCost);

                multipleProductRepository.save(product);


                logic.addNewNotification(logic.extractMainImage(product.getImages(),null),
                        String.format("Multi variant product %s material price have changed",product.getProductName()),
                        "Product material cost have changed the actual cost didn't please check the price and change the price of the actual product if necessary",null,
                        NotificationTypes.PRICE,
                        s,
                        null,
                        material.getUser().getId());

            }




        }


    }


    // ===============================================================
    // low material notification
    // ===============================================================
    @Scheduled(cron = "0 */100 * * * MON-FRI")
    @Transactional
    public void lowMaterialChecks(){



    }



    // ===============================================================
    // Timed maximize order thingy
    // ===============================================================

    @Scheduled(cron = "0 */60 * * * MON-FRI")
    @Transactional
    public void startTheMaximeOrder(){

        List<Long> users = userRepository.getIdsOfTheOrderProcessing(OrderProcessing.MAXIMIZE_ORDER.toString());



        if(users.isEmpty()){
            return;
        }

        for(var us : users){

            List<Orders> newOrders = orderRepository.getAllNewOrder(us);

            List<Materials> allMaterials = materialRepository.getAllMaterials(us);

            orderAcceptingModels.acceptOrdersAsManyAsPossible(newOrders,allMaterials);

        }
    }



    // ===============================================================
    // Timed recheck if possible to make orders that were marked as lack of supply
    // ===============================================================
    @Scheduled(cron = "0 */30 * * * MON-FRI")
    @Transactional
    public void startLackOfSupply(){

        List<Long> users = userRepository.getIdsOfAdmins();



        if(users.isEmpty()){
            return;
        }

        for(var us : users){

            List<Orders> newOrders = orderRepository.getAllLackOfSupplyOrders(us);

            List<Materials> allMaterials = materialRepository.getAllMaterials(us);

            orderAcceptingModels.acceptOrdersAsManyAsPossible(newOrders,allMaterials);

        }
    }

}
