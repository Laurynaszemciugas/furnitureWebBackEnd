package com.example.jwt_demo.controller;

import com.example.jwt_demo.DTOS.Order.OrdersFeedData;
import com.example.jwt_demo.Entity.Employee;
import com.example.jwt_demo.Entity.EmployeeJoin.OrderEmployees;
import com.example.jwt_demo.Entity.OrderJoin.OrderProducts;
import com.example.jwt_demo.Entity.Orders;
import com.example.jwt_demo.Entity.Product;
import com.example.jwt_demo.Enums.OrderStatus;
import com.example.jwt_demo.GlobalExseptions.Exseptions.ValidationException;
import com.example.jwt_demo.repository.EmployeeRepository;
import com.example.jwt_demo.repository.OrderRepository;
import com.example.jwt_demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    EmployeeRepository employeeRepository;


    @GetMapping("/getAllOrders/{orderStatusChoice}/{priceFromChoice}/{priceToChoice}/{dateFromChoice}/{dateToChoice}/{amountOfProductsChoice}/{pageChoice}/{pageCountChoice}")
    public ResponseEntity<List<OrdersFeedData>> getAllOrders(
            @PathVariable  OrderStatus orderStatusChoice,
            @PathVariable Double priceFromChoice,
            @PathVariable Double priceToChoice,
            @PathVariable LocalDate dateFromChoice,
            @PathVariable LocalDate dateToChoice,
            @PathVariable Long amountOfProductsChoice,
            @PathVariable int pageChoice,
            @PathVariable int pageCountChoice
    ) {

        System.out.println(priceFromChoice);
        System.out.println(dateFromChoice);
        System.out.println(orderStatusChoice);


        if(orderStatusChoice.equals(OrderStatus.ALL)){
            orderStatusChoice = null;
        }

        if(priceFromChoice == 0.0){
            priceFromChoice = null;
        }
        if(priceToChoice == 0.0){
            priceToChoice = null;
        }
        if(amountOfProductsChoice == 0){
            amountOfProductsChoice = null;
        }



        String fromInput = String.format("%s %s", "2025-02-02", "13:42:46.614631");
        String toInput = String.format("%s %s", "2050-12-20", "13:42:46.614631");

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

        LocalDateTime fromDate = LocalDateTime.parse(fromInput, formatter);
        LocalDateTime toDate = LocalDateTime.parse(toInput, formatter);

        return ResponseEntity.ok(
                orderRepository.getOrderData(
                        orderStatusChoice,
                        priceFromChoice,
                        priceToChoice,
                        fromDate,
                        toDate,
                        amountOfProductsChoice,
                        PageRequest.of(pageChoice, pageCountChoice)
                )
        );
    }


    @GetMapping("/getOrderFromId/{id}")
    public ResponseEntity<Orders> getOrderFromId(@PathVariable Long id){
        return ResponseEntity.ok(orderRepository.findById(id).orElseThrow());
    }

    @PostMapping("/saveModifiedOrder")
    public ResponseEntity<String> saveModifiedOrder(@RequestBody Orders order){

        Orders sameExistingOrder = orderRepository.findById(order.getId()).orElseThrow();

        sameExistingOrder.getProductsData().clear();
        sameExistingOrder.getEmployees().clear();



        double totalPrice = 0.0;

        for(var s : order.getProductsData()) {
            Long productId = s.getProduct().getId();
            Product existingProduct = productRepository.findById(productId).orElseThrow();

            if (s.getAmountOfProduct() <= 0 && s.getAmountOfProduct() > 101) {
                s.setAmountOfProduct(1l);
                throw  new ValidationException("Product quantity can only be from 1 to 100");
            }
            totalPrice += existingProduct.getPrice() * s.getAmountOfProduct();
            OrderProducts orderProducts = new OrderProducts();

            System.out.println(s.getAmountOfProduct());

            orderProducts.setAmountOfProduct(s.getAmountOfProduct());
            orderProducts.setOrder(sameExistingOrder);
            orderProducts.setProduct(existingProduct);
            orderProducts.setCost(existingProduct.getPrice()); // calculate plus tax stuff


            sameExistingOrder.getProductsData().add(orderProducts);
        }


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







        orderRepository.save(sameExistingOrder);


        return ResponseEntity.ok(String.format("ORD-%d %s",order.getId(), "was modified and saved successfully"));
    }







}
