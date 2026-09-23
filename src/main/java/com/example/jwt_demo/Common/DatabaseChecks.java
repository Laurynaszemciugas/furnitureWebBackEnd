package com.example.jwt_demo.Common;

import com.example.jwt_demo.Entity.*;
import com.example.jwt_demo.Entity.OrderJoin.OrderProducts;
import com.example.jwt_demo.Entity.ProductJoin.ProductMaterials;
import com.example.jwt_demo.Enums.*;
import com.example.jwt_demo.FilterDTO.Order.OrderFilterHolder;
import com.example.jwt_demo.GlobalExseptions.Exseptions.ValidationException;
import com.example.jwt_demo.controller.EmailSenderContoller;
import com.example.jwt_demo.controller.OrderController;
import com.example.jwt_demo.repository.*;
import com.example.jwt_demo.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;

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

    @Autowired
    ActionMaker actionMaker;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    EmailSenderContoller emailSenderContoller;

    @Autowired
    ProvidedDataChecker providedDataChecker;


    StringBuilder message = new StringBuilder();

    Long itemCount = 0L;


    public void addReserveFromCreatedOrder(Long orderId) {

        Orders order = orderRepository.findById(orderId).orElseThrow();

        for (var productData : order.getProductsData()) {


            Product product = productRepository.findById(productData.getProduct().getId()).orElseThrow();
            Long amountOfProductTaken = productData.getAmountOfProduct();
            for (var materials : product.getMaterials()) {

                Long materialNeededForOne = materials.getAmountUsed();

                Materials material = materialRepository.findById(materials.getMaterials().getId()).orElseThrow();
                Long materialStock = material.getInStock();

                if (materialStock - (materialNeededForOne * amountOfProductTaken) < 0) {
                    order.setOrderStatus(OrderStatus.LACK_OF_SUPPLY);
                    orderRepository.save(order);
                    //throw new ValidationException("Order is not possible it was set to LACK OF SUPPLY", Warnings.WARNING);
                    return;
                }

                else{

                    order.setOrderStatus(OrderStatus.AWAITING_CONFIRMATION);
                    orderRepository.save(order);
                }

                material.setInStock(materialStock - (materialNeededForOne * amountOfProductTaken));
                material.setReserved(material.getReserved() + (materialStock - (materialStock - (materialNeededForOne * amountOfProductTaken))));

                materialRepository.save(material);


            }


        }


    }

    public void orderFinishedDeductReserve(Long orderId) {

        Orders order = orderRepository.findById(orderId).orElseThrow();

        for (var orderProducts : order.getProductsData()) {
            Product product = productRepository.findById(orderProducts.getProduct().getId()).orElseThrow();
            for (var mats : product.getMaterials()) {

                Materials material = mats.getMaterials();

                Long reserved = material.getReserved();

                Long totalAmountOfMaterials = orderProducts.getAmountOfProduct() * mats.getAmountUsed();

                material.setReserved(reserved - totalAmountOfMaterials);

                materialRepository.save(material);

            }
        }







    }


//    for(var productData : order.getProductsData()){
//
//        Long amountOfProductTaken = productData.getAmountOfProduct();
//
//
//        Product product = productRepository.findById(productData.getProduct().getId()).orElseThrow();
//
//        Long productInStock = product.getStockQuantity();
//
//        product.setStockQuantity(productInStock - amountOfProductTaken);
//
//
//

//    }


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
                Long lowestAmountToMake = 0L;

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





                    if (limitingMaterial == null && lowestAmountToMake == 0) {
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


                actionMaker.makeAction(String.format("Stock of %s was was changed",prods.getProductName()),s.getId(),null, ActionTrackerEnum.SYSTEM, ActionDesciptionEnum.System_Check);

            }


        }


    }


    public void calculateMaterialsStock(Long orderId) {

        System.out.println("cheking db stuff");

        Orders order = orderRepository.findById(orderId).orElseThrow();





        for (var prods : order.getProductsData()) {

            if (prods.getProduct().isStockCalculatedManually()) {
                continue;
            }

//            OrderProducts productOld = oldOrder.getProductsData()
//                    .stream()
//                    .filter(p -> p.getProduct().getId().equals(prods.getProduct().getId()))
//                    .findFirst()
//                    .orElse(null);

//            if(prods.getAmountOfProduct().equals(productOld.getAmountOfProduct())){
//                continue;
//            }

            Long amountOfProductTaken = prods.getAmountOfProduct();
            Long remainingProduct = prods.getProduct().getStockQuantity();
//
//// seems useless i have product update btw
//            Product userDrivenProduct = prods.getProduct();
//
//            userDrivenProduct.setStockQuantity(
//                    Math.abs(amountOfProductTaken - remainingProduct)
//            );
//
//            productRepository.save(userDrivenProduct);





            for (var mats : prods.getProduct().getMaterials()) {


                Materials mat = materialRepository.findById(mats.getMaterials().getId()).orElseThrow();

                Long materialUsed = mats.getAmountUsed();

                Long getStock = mat.getInStock();


                Long newStock = getStock - (materialUsed * amountOfProductTaken);

                if (newStock < 0) {
                    newStock = 0L;
                }

                //mat.setInStock(newStock);

                Long lowThreshold = mat.getMinThresHold();

                if (getStock > lowThreshold) {
                    mat.setStock(Stock.In_Stock);
                }
                if (getStock <= lowThreshold) {
                    mat.setStock(Stock.Low_Stock);
                }
                if (getStock == 0) {
                    mat.setStock(Stock.No_Stock);
                }

                materialRepository.save(mat);


            }


        }


    }


    public void checkIfOrderPossible(Long orderId, Orders oldOrder){

        Orders order = orderRepository.findById(orderId).orElseThrow();

        for (var prods : order.getProductsData()) {

            OrderProducts old = oldOrder.getProductsData().stream().filter(p->p.getProduct().getId().equals(prods.getProduct().getId())).findFirst().orElse(null);

            if (prods.getProduct().isStockCalculatedManually()) {

                Long amountWasUsed;
                if(old != null) {
                    amountWasUsed = old.getAmountOfProduct();
                }
                else{
                    amountWasUsed = 0L;
                }



                Long amountUsedUpdated = prods.getAmountOfProduct();
                Long productStock = prods.getProduct().getStockQuantity();

                Long taken = amountUsedUpdated - amountWasUsed;


                System.out.println("===========================================================");

                System.out.println("Amount was used so old order - " + amountWasUsed);
                System.out.println("Amount new used - " + amountUsedUpdated);
                System.out.println("Amount taken - " + taken);

                System.out.println("===========================================================");

                System.out.println("product stock - " + productStock);


               if(taken > productStock){

                   String productName = prods.getProduct().getProductName();


                   throw new ValidationException("Order cannot be filled due to product stock (Manually set) [" + productName +"]", Warnings.ERROR);
               }


                continue;
            }

            Long amountOfProductTaken = prods.getAmountOfProduct();
            Long remainingProduct = prods.getProduct().getStockQuantity();



            if(old != null) {

                amountOfProductTaken = amountOfProductTaken - old.getAmountOfProduct();
            }

            if (amountOfProductTaken > remainingProduct) {
                String productName = prods.getProduct().getProductName();

                throw new ValidationException("Order cannot be filled due to product stock [" + productName +"]", Warnings.ERROR);
            }
        }
    }

    public void checkIfOrderDoesntTakeTooMuchSupply(Long orderId, Orders oldOrder){



        Orders newOrder = orderRepository.findById(orderId).orElseThrow();

        for(var productNew : newOrder.getProductsData()) {


            if(productNew.getProduct().isStockCalculatedManually()){
                continue;
            }

            OrderProducts productOld = oldOrder.getProductsData()
                    .stream()
                    .filter(p -> p.getProduct().getId().equals(productNew.getProduct().getId()))
                    .findFirst()
                    .orElse(null);

            for (var material : productNew.getProduct().getMaterials()) {

                Long newOrderMaterialCount = 0L;
                Long oldOrderMaterialCount = 0L;
                Long totalMaterialStock = 0L;

                if(productOld != null){
                    oldOrderMaterialCount = productOld.getAmountOfProduct() * material.getAmountUsed();
                }

                newOrderMaterialCount = productNew.getAmountOfProduct() * material.getAmountUsed();
                totalMaterialStock = material.getMaterials().getInStock();

                if(newOrderMaterialCount == oldOrderMaterialCount){
                    continue;
                }

                Long taken = newOrderMaterialCount - oldOrderMaterialCount;

                if(taken > totalMaterialStock){
                    throw new ValidationException("Order cannot be filled due chosen products combined materials are exeeding the storage amount", Warnings.ERROR);
                }


            }





        }
    }



    public void checkNewAddedOrder(Long orderId, boolean modifyStock){


        System.out.println("checking new order");

        Orders newOrder = orderRepository.findById(orderId).orElseThrow();


        for(var s : newOrder.getProductsData()){


            if (s.getProduct().isStockCalculatedManually()) {

                Product manuallySetProduct = productRepository.findById(s.getProduct().getId()).orElseThrow();

                Long productStock = manuallySetProduct.getStockQuantity();
                Long takenProductCount = s.getAmountOfProduct();

                Long stock = productStock - takenProductCount;




                if(stock < 0){

                    newOrder.setServerNote("Order is not possible due to materials shortage");
                    orderRepository.save(newOrder);
                    break;
                }

                if(modifyStock) {
                    manuallySetProduct.setStockQuantity(stock);

                    productRepository.save(manuallySetProduct);
                }

                continue;
            }


            for (var material : s.getProduct().getMaterials()) {




                Materials newlyAddedProductsMaterial = materialRepository.findById(material.getMaterials().getId()).orElseThrow();
                Long materialStock = newlyAddedProductsMaterial.getInStock();
                Long takenProductCount = s.getAmountOfProduct();
                Long amountMaterialNeededForOneProduct = material.getAmountUsed();

                Long stock = materialStock - (takenProductCount * amountMaterialNeededForOneProduct);



                System.out.println("stock levels");


                System.out.println(materialStock + " " + takenProductCount + "  " + amountMaterialNeededForOneProduct);
                System.out.println(stock);

                if (stock < 0) {

                    newOrder.setServerNote("Order is not possible due to materials shortage");
                    orderRepository.save(newOrder);
                    break;

                }

                if(modifyStock) {
                    newlyAddedProductsMaterial.setInStock(stock);
                    materialRepository.save(newlyAddedProductsMaterial);
                    logic.materialMovementTracker(newOrder.getUser().getId(),newOrder.getId(), material.getMaterials().getId(), materialStock, stock);
                }









            }


        }



    }



    public void checkModifiedOrders(Long orderId, Orders oldOrder) {

        // order possibillity checks

        checkIfOrderDoesntTakeTooMuchSupply(orderId,oldOrder);
        checkIfOrderPossible(orderId,oldOrder);


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

                    // add material reserve increase it

                    Long reserved = newlyAddedProductsMaterial.getReserved();

//                    System.out.println("Reserved " +  reserved);
//
//                    System.out.println("material stock was " + materialStock);
//                    System.out.println("material stock new " + stock);
//
//
//                    System.out.println("Diff " + (materialStock - stock));
//
//
//                    System.out.println("New reserve " + reserved + (materialStock - stock));


                    newlyAddedProductsMaterial.setReserved(reserved + (materialStock - stock));


                    newlyAddedProductsMaterial.setInStock(stock);

                    materialRepository.save(newlyAddedProductsMaterial);


                    // check if order is getting new value
                    logic.materialMovementTracker(newOrder.getUser().getId(),newOrder.getId(), material.getMaterials().getId(), materialStock, stock);



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


                        // remove material reserve decrease it

                        Long reserved = newlyAddedProductsMaterial.getReserved();

                        newlyAddedProductsMaterial.setReserved(reserved + (materialStock - stock));


                        newlyAddedProductsMaterial.setInStock(stock);

                        materialRepository.save(newlyAddedProductsMaterial);

                        // check if order is getting new value
                        logic.materialMovementTracker(newOrder.getUser().getId(),newOrder.getId(), material.getMaterials().getId(), materialStock, stock);


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
                        System.out.println("Product is new but it was fixed my other method skipped");
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

                        // remove or add  material reserve

                        Long reserved = newlyAddedProductsMaterial.getReserved();

                        newlyAddedProductsMaterial.setReserved(reserved + (materialStock - stock));


                        newlyAddedProductsMaterial.setInStock(stock);

                        materialRepository.save(newlyAddedProductsMaterial);

                        logic.materialMovementTracker(newOrder.getUser().getId(),newOrder.getId(), material.getMaterials().getId(), materialStock, stock);

                    }








                }





        }



        // ===================================== CHECK PRIORITY =======================================================================
    public void checkPriority(CustomUserDetails user, boolean checkAll) {

        // ============================================================
        // CHECK ONLY THE CURRENT USER
        // ============================================================

        if (!checkAll) {

            Long id;

            if (user.getRole().equals(Role.EMPLOYEE)) {

                Long employeeId = employeeRepository.employeeId(user.getId());

                Employee employee = employeeRepository
                        .findById(employeeId)
                        .orElseThrow();

                id = employee.getUser().getId();

            } else {

                id = user.getId();
            }

            System.out.println("Starting the priority check");

            orderRepository.checkThePriority(id);

            System.out.println("Finished the priority check");

            return;
        }


        // ============================================================
        // CHECK ALL USERS
        // ============================================================

        List<User> users = userRepository.findAll();


        // First update priorities for every user
        for (User currentUser : users) {

            orderRepository.checkThePriority(
                    currentUser.getId()
            );
        }


        // ============================================================
        // FIND ORDERS AND SEND EMAILS
        // ============================================================

        for (User currentUser : users) {

            List<Orders> orders =
                    orderRepository.findOrdersThatNeedToBeDone(
                            currentUser.getId()
                    );


            // Nothing to notify about
            if (orders.isEmpty()) {
                continue;
            }


            String emailContent =
                    buildPriorityEmail(orders);

            String userEmail =
                    currentUser.getGmail();

            long orderCount =
                    orders.size();


            // Copy values before async execution
            CompletableFuture.runAsync(() ->
                    emailSenderContoller.stockWarning(
                            userEmail,
                            emailContent,
                            orderCount
                    )
            );
        }
    }

    private String buildPriorityEmail(List<Orders> orders) {

        StringBuilder message = new StringBuilder();


        // ============================================================
        // HEADER
        // ============================================================

        message.append("""
            <!DOCTYPE html>
            <html>

            <head>
                <meta charset="UTF-8">
                <title>Orders Requiring Attention</title>
            </head>

            <body style="
                margin: 0;
                padding: 0;
                background-color: #f4f6f8;
                font-family: Arial, Helvetica, sans-serif;
                color: #202124;
            ">

            <div style="
                max-width: 850px;
                margin: 30px auto;
                background-color: #ffffff;
                border-radius: 12px;
                overflow: hidden;
                box-shadow: 0 2px 12px rgba(0,0,0,0.08);
            ">

                <!-- HEADER -->

                <div style="
                    background-color: #2275F3;
                    color: #ffffff;
                    padding: 25px 30px;
                ">

                    <h1 style="
                        margin: 0;
                        font-size: 24px;
                    ">
                        Furniture Management System
                    </h1>

                    <p style="
                        margin: 8px 0 0;
                        font-size: 14px;
                        opacity: 0.9;
                    ">
                        Order attention notification
                    </p>

                </div>


                <!-- CONTENT -->

                <div style="padding: 30px;">

                    <!-- WARNING -->

                    <div style="
                        background-color: #fff7e6;
                        border-left: 5px solid #f59e0b;
                        border-radius: 6px;
                        padding: 16px 18px;
                        margin-bottom: 28px;
                    ">

                        <div style="
                            font-size: 18px;
                            font-weight: bold;
                            color: #92400e;
                        ">
                            ⚠ Orders require attention
                        </div>

                        <p style="
                            margin: 7px 0 0;
                            color: #78350f;
                            font-size: 14px;
                        ">
                            The system found
                            <strong>%d</strong>
                            orders with high priority or overdue status.
                        </p>

                    </div>
            """.formatted(orders.size()));


        // ============================================================
        // ORDERS
        // ============================================================

        for (Orders order : orders) {

            message.append("""
        
        <!-- ORDER -->

        <div style="
            border: 1px solid #e1e5ea;
            border-radius: 10px;
            margin-bottom: 25px;
            overflow: hidden;
        ">

            <!-- ORDER HEADER -->

            <div style="
                background-color: #f8fafc;
                padding: 18px 20px;
                border-bottom: 1px solid #e1e5ea;
            ">

                <div style="
                    font-size: 20px;
                    font-weight: bold;
                    color: #1f2937;
                ">
                    Order #%d
                </div>


                <div style="
                    margin-top: 10px;
                    font-size: 14px;
                    color: #6b7280;
                ">

                    Customer:

                    <strong style="color: #374151;">
                        %s
                    </strong>

                    <br>

                    Email:

                    <span style="color: #374151;">
                        %s
                    </span>

                    <br>

                    Priority:

                    <strong style="
                        color: #dc2626;
                    ">
                        %s
                    </strong>

                    <br>

                    Estimated due date:

                    <strong style="
                        color: #374151;
                    ">
                        %s
                    </strong>

                </div>

            </div>


            <!-- ORDER BODY -->

            <div style="padding: 20px;">

                <h3 style="
                    margin: 0 0 12px 0;
                    font-size: 16px;
                    color: #1f2937;
                ">
                    Products
                </h3>


                <table style="
                    width: 100%%;
                    border-collapse: collapse;
                    font-size: 14px;
                ">

                    <thead>

                        <tr style="
                            background-color: #f1f5f9;
                            color: #475569;
                        ">

                            <th style="
                                padding: 11px;
                                text-align: left;
                                border-bottom: 1px solid #e2e8f0;
                            ">
                                Product
                            </th>

                            <th style="
                                padding: 11px;
                                text-align: center;
                                border-bottom: 1px solid #e2e8f0;
                            ">
                                Amount
                            </th>

                            <th style="
                                padding: 11px;
                                text-align: right;
                                border-bottom: 1px solid #e2e8f0;
                            ">
                                Material cost
                            </th>

                            <th style="
                                padding: 11px;
                                text-align: right;
                                border-bottom: 1px solid #e2e8f0;
                            ">
                                Total
                            </th>

                        </tr>

                    </thead>

                    <tbody>
""".formatted(
                    order.getId(),
                    order.getOrderCreatedByName(),
                    order.getOrderCreatedByGmail(),
                    order.getPriority(),
                    order.getEstimatedDueDate()
            ));


            // ========================================================
            // PRODUCTS
            // ========================================================

            for (var product : order.getProductsData()) {

                message.append("""
                            
                            <tr>

                                <td style="
                                    padding: 11px;
                                    border-bottom: 1px solid #e5e7eb;
                                    color: #1f2937;
                                ">
                                    <strong>%s</strong>
                                </td>

                                <td style="
                                    padding: 11px;
                                    text-align: center;
                                    border-bottom: 1px solid #e5e7eb;
                                    color: #374151;
                                ">
                                    %d
                                </td>

                                <td style="
                                    padding: 11px;
                                    text-align: right;
                                    border-bottom: 1px solid #e5e7eb;
                                    color: #374151;
                                ">
                                    %.2f €
                                </td>

                                <td style="
                                    padding: 11px;
                                    text-align: right;
                                    border-bottom: 1px solid #e5e7eb;
                                    color: #374151;
                                ">
                                    %.2f €
                                </td>

                            </tr>
                    """.formatted(
                        product.getProduct().getProductName(),
                        product.getAmountOfProduct(),
                        product.getProduct().getMaterialCost(),
                        product.getCost()
                ));
            }


            // ========================================================
            // EMPLOYEES
            // ========================================================

            message.append("""
                        
                        </tbody>

                    </table>


                    <!-- EMPLOYEES -->

                    <h3 style="
                        margin: 25px 0 12px 0;
                        font-size: 16px;
                        color: #1f2937;
                    ">
                        Assigned employees
                    </h3>


                    <div style="
                        background-color: #f8fafc;
                        border-radius: 7px;
                        padding: 12px 15px;
                    ">
                """);


            if (order.getEmployees() != null &&
                    !order.getEmployees().isEmpty()) {

                for (var employee : order.getEmployees()) {

                    message.append("""
                            
                            <div style="
                                padding: 5px 0;
                                font-size: 14px;
                                color: #374151;
                            ">
                                👤 %s
                            </div>
                    """.formatted(
                            employee.getEmployee().getFullName()
                    ));
                }

            } else {

                message.append("""
                            
                            <div style="
                                font-size: 14px;
                                color: #6b7280;
                            ">
                                No employees assigned
                            </div>
                    """);
            }


            // ========================================================
            // CLOSE ORDER
            // ========================================================

            message.append("""
                        
                    </div>

                </div>
            """);
        }


        // ============================================================
        // FOOTER
        // ============================================================

        message.append("""
            
                </div>


                <!-- FOOTER -->

                <div style="
                    background-color: #f8fafc;
                    border-top: 1px solid #e5e7eb;
                    padding: 20px 30px;
                    text-align: center;
                    color: #6b7280;
                    font-size: 12px;
                ">

                    This is an automated message from the
                    <strong>Furniture Management System</strong>.

                    <br><br>

                    Please review the listed orders in the system.

                </div>

            </div>

            </body>
            </html>
            """);


        return message.toString();
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
























