package com.example.jwt_demo.service.Order;

import com.example.jwt_demo.Common.ActionMaker;
import com.example.jwt_demo.Common.DatabaseChecks;
import com.example.jwt_demo.Common.ErrorResponse;
import com.example.jwt_demo.Common.OrderAcceptance.OrderAcceptingModels;
import com.example.jwt_demo.Entity.Employee;
import com.example.jwt_demo.Entity.EmployeeJoin.OrderEmployees;
import com.example.jwt_demo.Entity.OrderJoin.OrderProducts;
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
    public ResponseEntity<ErrorResponse> saveModifiedOrder(Orders order, CustomUserDetails user){

        Orders sameExistingOrder = orderRepository.findById(order.getId()).orElseThrow();
        Orders nonModified = copyOrder.copyOrder(sameExistingOrder);


//        System.out.println("sssssssssssssssssssssssssssssssssss");
//        System.out.println(sameExistingOrder.getProductsData().size());
//        for(var s : nonModified.getProductsData()){
//            for(var ss : s.getOrderSteps()){
//                System.out.println(ss.getStepName());
//            }
//        }


        if(order.getBillingAddress().isEmpty() || order.getBillingAddress() == null){
            throw  new ValidationException("Address is required", Warnings.ERROR);
        }

        sameExistingOrder.setBillingAddress(order.getBillingAddress());



        double totalPrice = 0.0;


        if(order.getProductsData().isEmpty() || order.getProductsData() == null){
            throw  new ValidationException("Existing order cannot be without products ", Warnings.ERROR);
        }

        List<OrderProducts> products = new ArrayList<>();
        sameExistingOrder.getProductsData().clear();
        for(var s : order.getProductsData()) {
            Long productId = s.getProduct().getId();
            Product existingProduct = productRepository.findById(productId).orElseThrow();

            if (s.getAmountOfProduct() <= 0 || s.getAmountOfProduct() >= 100) {
                throw  new ValidationException("Product quantity can only be from 1 to 99", Warnings.ERROR);
            }
            totalPrice += existingProduct.getPrice() * s.getAmountOfProduct();
            OrderProducts orderProducts = new OrderProducts();
            orderProducts.setProduct(existingProduct);
            orderProducts.setOrder(sameExistingOrder);
            orderProducts.setCost(orderAcceptingModels.materialCost(s.getProduct().getId(), s.getAmountOfProduct()));
            orderProducts.setAmountOfProduct(s.getAmountOfProduct());
            products.add(orderProducts);
        }

        sameExistingOrder.getProductsData().addAll(products);

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
        databaseChecks.calculateProductsStock(null,false);

        orderRepository.save(sameExistingOrder);

        actionMaker.makeAction(String.format("ORD-%d %s",order.getId(), "was modified and saved successfully"),user.getId(),null, ActionTrackerEnum.USER, ActionDesciptionEnum.Order_Updated);


        orderRepository.incrementProductsFinished(order.getId());

        return ResponseEntity.ok(new ErrorResponse(String.format("ORD-%d %s",order.getId(), "was modified and saved successfully"),Warnings.OK));
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
