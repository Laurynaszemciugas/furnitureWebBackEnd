package com.example.jwt_demo.service.Order;

import com.example.jwt_demo.Common.ActionMaker;
import com.example.jwt_demo.Common.DatabaseChecks;
import com.example.jwt_demo.Common.ErrorResponse;
import com.example.jwt_demo.Common.OrderAcceptance.OrderAcceptingModels;
import com.example.jwt_demo.Entity.Employee;
import com.example.jwt_demo.Entity.EmployeeJoin.OrderEmployees;
import com.example.jwt_demo.Entity.OrderJoin.OrderProducts;
import com.example.jwt_demo.Entity.OrderJoin.OrderStepsToComplete;
import com.example.jwt_demo.Entity.Orders;
import com.example.jwt_demo.Entity.Product;
import com.example.jwt_demo.Entity.User;
import com.example.jwt_demo.Enums.*;
import com.example.jwt_demo.GlobalExseptions.Exseptions.ValidationException;
import com.example.jwt_demo.repository.EmployeeRepository;
import com.example.jwt_demo.repository.OrderRepository;
import com.example.jwt_demo.repository.ProductRepository;
import com.example.jwt_demo.repository.UserRepository;
import com.example.jwt_demo.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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


    // save modified order
// save modified order
    @Transactional
    public ResponseEntity<ErrorResponse> saveModifiedOrder(
            Orders order,
            CustomUserDetails user
    ) {

        Orders sameExistingOrder =
                orderRepository.findById(order.getId()).orElseThrow();

        Orders nonModified =
                copyOrder.copyOrder(sameExistingOrder);


        // ============================================================
        // VALIDATION
        // ============================================================

        if (order.getBillingAddress() == null ||
                order.getBillingAddress().isEmpty()) {

            throw new ValidationException(
                    "Address is required",
                    Warnings.ERROR
            );
        }

        if (order.getProductsData() == null ||
                order.getProductsData().isEmpty()) {

            throw new ValidationException(
                    "Existing order cannot be without products ",
                    Warnings.ERROR
            );
        }

        if (order.getEmployees() == null ||
                order.getEmployees().isEmpty()) {

            throw new ValidationException(
                    "Existing order cannot be without employees ",
                    Warnings.ERROR
            );
        }


        // ============================================================
        // BASIC ORDER INFORMATION
        // ============================================================

        sameExistingOrder.setBillingAddress(
                order.getBillingAddress()
        );


        // ============================================================
        // PRODUCTS
        // ============================================================

        double totalPrice = 0.0;


        /*
         * First find products which were REMOVED.
         *
         * We DO NOT delete them from the database because their
         * OrderStepsToComplete / WorkDone must remain.
         *
         * Instead we disconnect the OrderProducts from this order.
         */
        for (OrderProducts existingProduct :
                new ArrayList<>(sameExistingOrder.getProductsData())) {

            Long existingProductId =
                    existingProduct.getProduct().getId();

            boolean stillExists =
                    order.getProductsData()
                            .stream()
                            .anyMatch(incoming ->
                                    incoming.getProduct() != null &&
                                            incoming.getProduct().getId()
                                                    .equals(existingProductId)
                            );

            if (!stillExists) {

                /*
                 * IMPORTANT:
                 *
                 * Do NOT delete existingProduct.
                 *
                 * It still has:
                 *
                 * OrderProducts
                 *      ↓
                 * OrderStepsToComplete
                 *      ↓
                 * WorkDone
                 *
                 * We only remove it from this order.
                 */

                existingProduct.setOrder(null);

                sameExistingOrder
                        .getProductsData()
                        .remove(existingProduct);
            }
        }


        /*
         * Now process the products which came from the frontend.
         */
        for (var s : order.getProductsData()) {

            Long productId =
                    s.getProduct().getId();

            Product existingProduct =
                    productRepository
                            .findById(productId)
                            .orElseThrow();


            if (s.getAmountOfProduct() <= 0 ||
                    s.getAmountOfProduct() >= 100) {

                throw new ValidationException(
                        "Product quantity can only be from 1 to 99",
                        Warnings.ERROR
                );
            }


            totalPrice +=
                    existingProduct.getPrice()
                            * s.getAmountOfProduct();


            /*
             * Find the EXISTING OrderProducts entity.
             *
             * We search the managed entity from the database,
             * NOT the object received from the frontend.
             */
            OrderProducts existingOrderProduct =
                    sameExistingOrder
                            .getProductsData()
                            .stream()
                            .filter(p ->
                                    p.getProduct() != null &&
                                            p.getProduct()
                                                    .getId()
                                                    .equals(productId)
                            )
                            .findFirst()
                            .orElse(null);


            if (existingOrderProduct != null) {

                // ====================================================
                // EXISTING PRODUCT
                // ====================================================

                /*
                 * Keep the existing OrderProducts ID.
                 *
                 * Therefore its existing steps remain attached.
                 */

                existingOrderProduct.setAmountOfProduct(
                        s.getAmountOfProduct()
                );

                existingOrderProduct.setCost(
                        orderAcceptingModels.materialCost(
                                productId,
                                s.getAmountOfProduct()
                        )
                );

                /*
                 * DO NOT:
                 *
                 * existingOrderProduct.setOrderSteps(...)
                 *
                 * DO NOT recreate the steps.
                 */


            } else {

                // ====================================================
                // NEW PRODUCT
                // ====================================================

                OrderProducts orderProducts =
                        new OrderProducts();

                orderProducts.setProduct(
                        existingProduct
                );

                orderProducts.setOrder(
                        sameExistingOrder
                );

                orderProducts.setCost(
                        orderAcceptingModels.materialCost(
                                productId,
                                s.getAmountOfProduct()
                        )
                );

                orderProducts.setAmountOfProduct(
                        s.getAmountOfProduct()
                );


                /*
                 * Create the steps for this NEW product.
                 */
                List<OrderStepsToComplete> orderSteps =
                        new ArrayList<>();

                Long sizeOfTheSteps =
                        Long.valueOf(
                                existingProduct.getSteps().size()
                        );

                Long i = 0L;


                for (var step : existingProduct.getSteps()) {

                    OrderStepsToComplete orderStep =
                            new OrderStepsToComplete();


                    orderStep.setProductFinishStepStatus(
                            ProductFinishStepStatus.NOT_STARTED
                    );

                    orderStep.setStepsNeeded(
                            orderProducts.getAmountOfProduct()
                    );

                    orderStep.setStepsCompleted(0L);


                    if (existingProduct.isStockCalculatedManually()) {

                        orderStep.setStepId(1L);

                        orderStep.setStepName(
                                "Package the product"
                        );

                        orderStep.setStepDescription(
                                "Package the product using the styro foam bubble rap"
                        );

                        orderStep.setOrderProducts(
                                orderProducts
                        );

                        orderSteps.add(orderStep);

                        break;

                    } else {

                        orderStep.setStepRealId(
                                step.getId()
                        );

                        orderStep.setStepId(
                                step.getStepId()
                        );

                        orderStep.setStepName(
                                step.getStepName()
                        );

                        orderStep.setStepDescription(
                                step.getStepDescription()
                        );

                        orderStep.setOrderProducts(
                                orderProducts
                        );

                        orderSteps.add(orderStep);
                    }


                    i++;


                    /*
                     * Add package step at the end.
                     */
                    if (i.equals(sizeOfTheSteps)) {

                        OrderStepsToComplete packageStep =
                                new OrderStepsToComplete();

                        packageStep.setProductFinishStepStatus(
                                ProductFinishStepStatus.NOT_STARTED
                        );

                        packageStep.setStepsNeeded(
                                orderProducts.getAmountOfProduct()
                        );

                        packageStep.setStepsCompleted(0L);

                        packageStep.setStepId(
                                step.getStepId() + 1
                        );

                        packageStep.setStepName(
                                "Package the product"
                        );

                        packageStep.setStepDescription(
                                "Package the product using the styro foam bubble rap"
                        );

                        packageStep.setOrderProducts(
                                orderProducts
                        );

                        orderSteps.add(packageStep);
                    }
                }


                orderProducts.setOrderSteps(
                        orderSteps
                );


                /*
                 * Add the NEW product to the order.
                 */
                sameExistingOrder
                        .getProductsData()
                        .add(orderProducts);
            }
        }


        // ============================================================
        // EMPLOYEES
        // ============================================================

        sameExistingOrder.getEmployees().clear();

        for (var s : order.getEmployees()) {

            Long employeeId =
                    s.getEmployee().getId();

            Employee existingEmployee =
                    employeeRepository
                            .findById(employeeId)
                            .orElseThrow();


            OrderEmployees orderEmployees =
                    new OrderEmployees();

            orderEmployees.setOrder(
                    sameExistingOrder
            );

            orderEmployees.setEmployee(
                    existingEmployee
            );

            sameExistingOrder
                    .getEmployees()
                    .add(orderEmployees);
        }


        // ============================================================
        // OTHER ORDER INFORMATION
        // ============================================================

        sameExistingOrder.setTotalPrice(
                totalPrice
        );

        sameExistingOrder.setOrderNote(
                order.getOrderNote()
        );

        sameExistingOrder.setOrderStatus(
                order.getOrderStatus()
        );

        sameExistingOrder.setEstimatedDueDate(
                order.getEstimatedDueDate()
        );

        sameExistingOrder.setPayMethod(
                order.getPayMethod()
        );

        sameExistingOrder.setPayStatus(
                order.getPayStatus()
        );


        // ============================================================
        // DATABASE CHECKS
        // ============================================================

        databaseChecks.calculateMaterialsStock(
                order.getId()
        );

        databaseChecks.checkModifiedOrders(
                sameExistingOrder.getId(),
                nonModified
        );

        databaseChecks.calculateProductsStock(
                null,
                false
        );


        // ============================================================
        // SAVE
        // ============================================================

        orderRepository.save(
                sameExistingOrder
        );


        actionMaker.makeAction(
                String.format(
                        "ORD-%d %s",
                        order.getId(),
                        "was modified and saved successfully"
                ),
                user.getId(),
                null,
                ActionTrackerEnum.USER,
                ActionDesciptionEnum.Order_Updated
        );


        orderRepository.incrementProductsFinished(
                order.getId()
        );


        return ResponseEntity.ok(
                new ErrorResponse(
                        String.format(
                                "ORD-%d %s",
                                order.getId(),
                                "was modified and saved successfully"
                        ),
                        Warnings.OK
                )
        );
    }



    public ResponseEntity<ErrorResponse> saveOrder(@RequestBody Orders order, CustomUserDetails userData){


        User user = userRepository.findById(userData.getId()).orElseThrow();

        User actualUser = userRepository.findById(user.getId()).orElseThrow();


        if(actualUser.getRole().equals(Role.ANONYMOUS)){

        }
        else if (actualUser.getRole().equals(Role.ADMIN)){


            OrderProcessing orderProcessing = actualUser.getUserSettingsList().getOrderProcessing();


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

        return ResponseEntity.ok(new ErrorResponse(String.format("Order created",order.getId()),Warnings.OK));

    }


}
