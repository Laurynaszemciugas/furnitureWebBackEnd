package com.example.jwt_demo.service.Order;

import com.example.jwt_demo.Common.ActionMaker;
import com.example.jwt_demo.Common.DatabaseChecks;
import com.example.jwt_demo.Common.ErrorResponse;
import com.example.jwt_demo.Common.OrderAcceptance.OrderAcceptingModels;
import com.example.jwt_demo.Entity.*;
import com.example.jwt_demo.Entity.EmployeeJoin.OrderEmployees;
import com.example.jwt_demo.Entity.OrderJoin.OrderProducts;
import com.example.jwt_demo.Enums.*;
import com.example.jwt_demo.GlobalExseptions.Exseptions.ValidationException;
import com.example.jwt_demo.repository.*;
import com.example.jwt_demo.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderSavedService {

    @Autowired
    DatabaseChecks databaseChecks;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    OrderAcceptingModels orderAcceptingModels;

    @Autowired
    ActionMaker actionMaker;

    @Autowired
    CopyOrder copyOrder;

    @Autowired
    MultipleProductRepository multipleProductRepository;




//
//    // save modified order
//    @Transactional
//    public ResponseEntity<ErrorResponse> saveModifiedOrder(
//            Orders order,
//            CustomUserDetails user
//    ) {
//
//        Orders sameExistingOrder =
//                orderRepository.findById(order.getId()).orElseThrow();
//
//        Orders nonModified =
//                copyOrder.copyOrder(sameExistingOrder);
//
//
//        // ============================================================
//        // VALIDATION
//        // ============================================================
//
//        if (order.getBillingAddress() == null ||
//                order.getBillingAddress().isEmpty()) {
//
//            throw new ValidationException(
//                    "Address is required",
//                    Warnings.ERROR
//            );
//        }
//
//        if (order.getProductsData() == null ||
//                order.getProductsData().isEmpty()) {
//
//            throw new ValidationException(
//                    "Existing order cannot be without products ",
//                    Warnings.ERROR
//            );
//        }
//
//        if (order.getEmployees() == null ||
//                order.getEmployees().isEmpty()) {
//
//            throw new ValidationException(
//                    "Existing order cannot be without employees ",
//                    Warnings.ERROR
//            );
//        }
//
//
//        // ============================================================
//        // BASIC ORDER INFORMATION
//        // ============================================================
//
//        sameExistingOrder.setBillingAddress(
//                order.getBillingAddress()
//        );
//
//
//        // ============================================================
//        // PRODUCTS
//        // ============================================================
//
//        double totalPrice = 0.0;
//
//
//        /*
//         * First find products which were REMOVED.
//         *
//         * We DO NOT delete them from the database because their
//         * OrderStepsToComplete / WorkDone must remain.
//         *
//         * Instead we disconnect the OrderProducts from this order.
//         */
//        for (OrderProducts existingProduct :
//                new ArrayList<>(sameExistingOrder.getProductsData())) {
//
//            Long existingProductId =
//                    existingProduct.getProduct().getId();
//
//            boolean stillExists =
//                    order.getProductsData()
//                            .stream()
//                            .anyMatch(incoming ->
//                                    incoming.getProduct() != null &&
//                                            incoming.getProduct().getId()
//                                                    .equals(existingProductId)
//                            );
//
//            if (!stillExists) {
//
//                /*
//                 * IMPORTANT:
//                 *
//                 * Do NOT delete existingProduct.
//                 *
//                 * It still has:
//                 *
//                 * OrderProducts
//                 *      ↓
//                 * OrderStepsToComplete
//                 *      ↓
//                 * WorkDone
//                 *
//                 * We only remove it from this order.
//                 */
//
//                existingProduct.setOrder(null);
//
//                sameExistingOrder
//                        .getProductsData()
//                        .remove(existingProduct);
//            }
//        }
//
//
//        /*
//         * Now process the products which came from the frontend.
//         */
//        for (var s : order.getProductsData()) {
//
//            Long productId =
//                    s.getProduct().getId();
//
//            Product existingProduct =
//                    productRepository
//                            .findById(productId)
//                            .orElseThrow();
//
//
//            if (s.getAmountOfProduct() <= 0 ||
//                    s.getAmountOfProduct() >= 100) {
//
//                throw new ValidationException(
//                        "Product quantity can only be from 1 to 99",
//                        Warnings.ERROR
//                );
//            }
//
//
//            totalPrice +=
//                    existingProduct.getPrice()
//                            * s.getAmountOfProduct();
//
//
//            /*
//             * Find the EXISTING OrderProducts entity.
//             *
//             * We search the managed entity from the database,
//             * NOT the object received from the frontend.
//             */
//            OrderProducts existingOrderProduct =
//                    sameExistingOrder
//                            .getProductsData()
//                            .stream()
//                            .filter(p ->
//                                    p.getProduct() != null &&
//                                            p.getProduct()
//                                                    .getId()
//                                                    .equals(productId)
//                            )
//                            .findFirst()
//                            .orElse(null);
//
//
//            if (existingOrderProduct != null) {
//
//                // ====================================================
//                // EXISTING PRODUCT
//                // ====================================================
//
//                /*
//                 * Keep the existing OrderProducts ID.
//                 *
//                 * Therefore its existing steps remain attached.
//                 */
//
//                existingOrderProduct.setAmountOfProduct(
//                        s.getAmountOfProduct()
//                );
//
//                existingOrderProduct.setCost(
//                        orderAcceptingModels.materialCost(
//                                productId,
//                                s.getAmountOfProduct()
//                        )
//                );
//
//                /*
//                 * DO NOT:
//                 *
//                 * existingOrderProduct.setOrderSteps(...)
//                 *
//                 * DO NOT recreate the steps.
//                 */
//
//
//            } else {
//
//                // ====================================================
//                // NEW PRODUCT
//                // ====================================================
//
//                OrderProducts orderProducts =
//                        new OrderProducts();
//
//                orderProducts.setProduct(
//                        existingProduct
//                );
//
//                orderProducts.setOrder(
//                        sameExistingOrder
//                );
//
//                orderProducts.setCost(
//                        orderAcceptingModels.materialCost(
//                                productId,
//                                s.getAmountOfProduct()
//                        )
//                );
//
//                orderProducts.setAmountOfProduct(
//                        s.getAmountOfProduct()
//                );
//
//
//                /*
//                 * Create the steps for this NEW product.
//                 */
//                List<OrderStepsToComplete> orderSteps =
//                        new ArrayList<>();
//
//                Long sizeOfTheSteps =
//                        Long.valueOf(
//                                existingProduct.getSteps().size()
//                        );
//
//                Long i = 0L;
//
//
//                for (var step : existingProduct.getSteps()) {
//
//                    OrderStepsToComplete orderStep =
//                            new OrderStepsToComplete();
//
//
//                    orderStep.setProductFinishStepStatus(
//                            ProductFinishStepStatus.NOT_STARTED
//                    );
//
//                    orderStep.setStepsNeeded(
//                            orderProducts.getAmountOfProduct()
//                    );
//
//                    orderStep.setStepsCompleted(0L);
//
//
//                    if (existingProduct.isStockCalculatedManually()) {
//
//                        orderStep.setStepId(1L);
//
//                        orderStep.setStepName(
//                                "Package the product"
//                        );
//
//                        orderStep.setStepDescription(
//                                "Package the product using the styro foam bubble rap"
//                        );
//
//                        orderStep.setOrderProducts(
//                                orderProducts
//                        );
//
//                        orderSteps.add(orderStep);
//
//                        break;
//
//                    } else {
//
//                        orderStep.setStepRealId(
//                                step.getId()
//                        );
//
//                        orderStep.setStepId(
//                                step.getStepId()
//                        );
//
//                        orderStep.setStepName(
//                                step.getStepName()
//                        );
//
//                        orderStep.setStepDescription(
//                                step.getStepDescription()
//                        );
//
//                        orderStep.setOrderProducts(
//                                orderProducts
//                        );
//
//                        orderSteps.add(orderStep);
//                    }
//
//
//                    i++;
//
//
//                    /*
//                     * Add package step at the end.
//                     */
//                    if (i.equals(sizeOfTheSteps)) {
//
//                        OrderStepsToComplete packageStep =
//                                new OrderStepsToComplete();
//
//                        packageStep.setProductFinishStepStatus(
//                                ProductFinishStepStatus.NOT_STARTED
//                        );
//
//                        packageStep.setStepsNeeded(
//                                orderProducts.getAmountOfProduct()
//                        );
//
//                        packageStep.setStepsCompleted(0L);
//
//                        packageStep.setStepId(
//                                step.getStepId() + 1
//                        );
//
//                        packageStep.setStepName(
//                                "Package the product"
//                        );
//
//                        packageStep.setStepDescription(
//                                "Package the product using the styro foam bubble rap"
//                        );
//
//                        packageStep.setOrderProducts(
//                                orderProducts
//                        );
//
//                        orderSteps.add(packageStep);
//                    }
//                }
//
//
//                orderProducts.setOrderSteps(
//                        orderSteps
//                );
//
//
//                /*
//                 * Add the NEW product to the order.
//                 */
//                sameExistingOrder
//                        .getProductsData()
//                        .add(orderProducts);
//            }
//        }
//
//
//        // ============================================================
//        // EMPLOYEES
//        // ============================================================
//
//        sameExistingOrder.getEmployees().clear();
//
//        for (var s : order.getEmployees()) {
//
//            Long employeeId =
//                    s.getEmployee().getId();
//
//            Employee existingEmployee =
//                    employeeRepository
//                            .findById(employeeId)
//                            .orElseThrow();
//
//
//            OrderEmployees orderEmployees =
//                    new OrderEmployees();
//
//            orderEmployees.setOrder(
//                    sameExistingOrder
//            );
//
//            orderEmployees.setEmployee(
//                    existingEmployee
//            );
//
//            sameExistingOrder
//                    .getEmployees()
//                    .add(orderEmployees);
//        }
//
//
//        // ============================================================
//        // OTHER ORDER INFORMATION
//        // ============================================================
//
//        sameExistingOrder.setTotalPrice(
//                totalPrice
//        );
//
//        sameExistingOrder.setOrderNote(
//                order.getOrderNote()
//        );
//
//        sameExistingOrder.setOrderStatus(
//                order.getOrderStatus()
//        );
//
//        sameExistingOrder.setEstimatedDueDate(
//                order.getEstimatedDueDate()
//        );
//
//        sameExistingOrder.setPayMethod(
//                order.getPayMethod()
//        );
//
//        sameExistingOrder.setPayStatus(
//                order.getPayStatus()
//        );
//
//
//        // ============================================================
//        // DATABASE CHECKS
//        // ============================================================
//
//        databaseChecks.calculateMaterialsStock(
//                order.getId()
//        );
//
//        databaseChecks.checkModifiedOrders(
//                sameExistingOrder.getId(),
//                nonModified
//        );
//
//        databaseChecks.calculateProductsStock(
//                null,
//                false
//        );
//
//
//        // ============================================================
//        // SAVE
//        // ============================================================
//
//        orderRepository.save(
//                sameExistingOrder
//        );
//
//
//        actionMaker.makeAction(
//                String.format(
//                        "ORD-%d %s",
//                        order.getId(),
//                        "was modified and saved successfully"
//                ),
//                user.getId(),
//                null,
//                ActionTrackerEnum.USER,
//                ActionDesciptionEnum.Order_Updated
//        );
//
//
//        orderRepository.incrementProductsFinished(
//                order.getId()
//        );
//
//
//        return ResponseEntity.ok(
//                new ErrorResponse(
//                        String.format(
//                                "ORD-%d %s",
//                                order.getId(),
//                                "was modified and saved successfully"
//                        ),
//                        Warnings.OK
//                )
//        );
//    }
























    // save modified order
    public ResponseEntity<ErrorResponse> saveModifiedOrder(Orders order, CustomUserDetails user){

        Orders sameExistingOrder = orderRepository.findById(order.getId()).orElseThrow();
        Orders nonModified = copyOrder.copyOrder(sameExistingOrder);


        if(order.getBillingAddress().isEmpty() || order.getBillingAddress() == null){
            throw  new ValidationException("Address is required", Warnings.ERROR);
        }

        sameExistingOrder.setBillingAddress(order.getBillingAddress());
        double totalPrice = 0.0;

//        if(order.getProductsData().isEmpty() || order.getProductsData() == null){
//            throw  new ValidationException("Existing order cannot be without products ", Warnings.ERROR);
//        }



        for (var existingProduct : new ArrayList<>(sameExistingOrder.getProductsData())) {

            if(existingProduct.getProductMultiple() != null){


                System.out.println("idsssssss");
                System.out.println(existingProduct.getProductMultiple().getId());

                Long existingProductId = existingProduct.getProductMultiple().getId();

                boolean stillExists = order.getProductsData().stream()
                        .anyMatch(newProduct ->
                                newProduct.getProductMultiple() != null && newProduct.getProductMultiple().getId().equals(existingProductId)
                        );

                if (!stillExists) {
                    existingProduct.setOrder(null);
                    sameExistingOrder.getProductsData().remove(existingProduct);
                }

            }
            else{

                Long existingProductId = existingProduct.getProduct().getId();

                boolean stillExists = order.getProductsData().stream()
                        .anyMatch(newProduct ->
                                newProduct.getProduct().getId().equals(existingProductId)
                        );

                if (!stillExists) {
                    existingProduct.setOrder(null);
                    sameExistingOrder.getProductsData().remove(existingProduct);
                }


            }


        }



        List<OrderProducts> products = sameExistingOrder.getProductsData();
        for(var s : order.getProductsData()) {


            Product product = null;

            if(s.getProduct() != null) {
                System.out.println("Product id");
                System.out.println(s.getProduct().getId());
            }

            if(s.getProductMultiple() != null) {
                System.out.println("Multi product id");
                System.out.println(s.getProductMultiple().getId());

                ProductMultiple productMultiple = multipleProductRepository.findById(s.getProductMultiple().getId()).orElseThrow();

                 product = productRepository.findById(productMultiple.getProduct().getId()).orElseThrow(()-> new ValidationException("Product not found", Warnings.ERROR));
            }

            if(s.getProductMultiple() != null){

                if (s.getAmountOfProduct() <= 0 || s.getAmountOfProduct() >= 100) {
                    throw  new ValidationException("Product quantity can only be from 1 to 99", Warnings.ERROR);
                }

                Long productId = s.getProductMultiple().getId();
                ProductMultiple existingProduct = multipleProductRepository.findById(productId).orElseThrow();

                totalPrice += existingProduct.getPrice() * s.getAmountOfProduct();


                OrderProducts orderProducts = products.stream()
                        .filter(p -> p.getProductMultiple() != null
                                && p.getProductMultiple().getId().equals(productId))
                        .findFirst()
                        .orElse(null);



                if(orderProducts != null){

                    orderProducts.setAmountOfProduct(s.getAmountOfProduct());
                    orderProducts.setProduct(product);
                    orderProducts.setCost(
                            orderAcceptingModels.materialCost(
                                    productId,
                                    s.getAmountOfProduct(),
                                    true
                            )
                    );
                }

                else{
                    orderProducts = new OrderProducts();
                    orderProducts.setProductMultiple(existingProduct);
                    orderProducts.setProduct(product);
                    orderProducts.setOrder(sameExistingOrder);
                    orderProducts.setCost(orderAcceptingModels.materialCost(s.getProductMultiple().getId(), s.getAmountOfProduct(),true));
                    orderProducts.setAmountOfProduct(s.getAmountOfProduct());
                    products.add(orderProducts);


                }

            }

            else{

                if (s.getAmountOfProduct() <= 0 || s.getAmountOfProduct() >= 100) {
                    throw  new ValidationException("Product quantity can only be from 1 to 99", Warnings.ERROR);
                }

                Long productId = s.getProduct().getId();
                Product existingProduct = productRepository.findById(productId).orElseThrow();

                totalPrice += existingProduct.getPrice() * s.getAmountOfProduct();


                OrderProducts orderProducts = products.stream()
                        .filter(p -> p.getProduct().getId().equals(productId))
                        .findFirst()
                        .orElse(null);


                if(orderProducts != null){
                    orderProducts.setAmountOfProduct(s.getAmountOfProduct());
                    orderProducts.setCost(
                            orderAcceptingModels.materialCost(
                                    productId,
                                    s.getAmountOfProduct(),
                                    false
                            )
                    );
                }

                else{
                    orderProducts = new OrderProducts();
                    orderProducts.setProduct(existingProduct);
                    orderProducts.setOrder(sameExistingOrder);
                    orderProducts.setCost(orderAcceptingModels.materialCost(s.getProduct().getId(), s.getAmountOfProduct(),false));
                    orderProducts.setAmountOfProduct(s.getAmountOfProduct());
                    products.add(orderProducts);


                }

            }

        }






        if(order.getEmployees().isEmpty() || order.getEmployees() == null){
            throw  new ValidationException("Existing order cannot be without employees ", Warnings.ERROR);
        }

        sameExistingOrder.getEmployees().clear();
        for(var s : order.getEmployees()){
            Long employeeId = s.getEmployee().getId();



            Employee existingEmployee = employeeRepository.findById(employeeId).orElseThrow();


            OrderEmployees orderEmployees = new OrderEmployees();
            orderEmployees.setOrder(sameExistingOrder);
            orderEmployees.setEmployee(existingEmployee);

            sameExistingOrder.getEmployees().add(orderEmployees);

        }

        sameExistingOrder.setTotalPrice(totalPrice);
        sameExistingOrder.setOrderNote(order.getOrderNote());
        sameExistingOrder.setOrderStatus(order.getOrderStatus());
        sameExistingOrder.setEstimatedDueDate(order.getEstimatedDueDate());
        sameExistingOrder.setPayMethod(order.getPayMethod());
        sameExistingOrder.setPayStatus(order.getPayStatus());

        databaseChecks.calculateMaterialsStock(order.getId());
        databaseChecks.checkModifiedOrders(sameExistingOrder.getId(),nonModified);
        databaseChecks.checkOrderSteps(sameExistingOrder.getId(),nonModified);


        databaseChecks.calculateProductsStock(null,false);

        orderRepository.saveAndFlush(sameExistingOrder);


        actionMaker.makeAction(String.format("ORD-%d %s",order.getId(), "was modified and saved successfully"),user.getId(),null, ActionTrackerEnum.USER, ActionDesciptionEnum.Order_Updated);


        orderRepository.incrementProductsFinished(order.getId());

        return ResponseEntity.ok(new ErrorResponse(String.format("ORD-%d %s",order.getId(), "was modified and saved successfully"),Warnings.OK));
    }



    public ResponseEntity<ErrorResponse> saveOrder(@RequestBody Orders order, CustomUserDetails userData){


        User user = userRepository.findById(userData.getId()).orElseThrow();




        if(user.getRole().equals(Role.ANONYMOUS)){

        }
        else if (user.getRole().equals(Role.ADMIN)){

            OrderProcessing orderProcessing = user.getUserSettingsList().getOrderProcessing();


            switch (orderProcessing){
                case FIRST_COME_FIRST_SERVE -> {
                    orderAcceptingModels.saveNewOrder(order,user);
                }
                case MAXIMIZE_ORDER -> {
                    Orders saveOrder = orderAcceptingModels.putInfoIntoNewOrder(order,user);
                    orderRepository.save(saveOrder);
                }

            }


        }

        orderRepository.incrementProductsFinished(order.getId());

        return ResponseEntity.ok(new ErrorResponse(String.format("Order created",order.getId()),Warnings.OK));

    }


}
