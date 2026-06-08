package com.example.jwt_demo.controller;

import com.example.jwt_demo.Common.Logic;
import com.example.jwt_demo.DTOS.Order.ConsumerData;
import com.example.jwt_demo.DTOS.Order.OrdersFeedData;
import com.example.jwt_demo.Entity.Employee;
import com.example.jwt_demo.Entity.EmployeeJoin.OrderEmployees;
import com.example.jwt_demo.Entity.OrderJoin.OrderProducts;
import com.example.jwt_demo.Entity.Orders;
import com.example.jwt_demo.Entity.Product;
import com.example.jwt_demo.Entity.User;
import com.example.jwt_demo.Enums.OrderStatus;
import com.example.jwt_demo.GlobalExseptions.Exseptions.ValidationException;
import com.example.jwt_demo.repository.EmployeeRepository;
import com.example.jwt_demo.repository.OrderRepository;
import com.example.jwt_demo.repository.ProductRepository;
import com.example.jwt_demo.repository.UserRepository;
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

    @Autowired
    UserRepository userRepository;

    @Autowired
    Logic logic;

    @GetMapping("/getAllOrders/{orderStatusChoice}/{priceFromChoice}/{priceToChoice}/{dateFromChoice}/{dateToChoice}/{amountOfProductsChoice}/{prompt}/{pageChoice}/{pageCountChoice}")
    public ResponseEntity<List<OrdersFeedData>> getAllOrders(
            @PathVariable  OrderStatus orderStatusChoice,
            @PathVariable Double priceFromChoice,
            @PathVariable Double priceToChoice,
            @PathVariable LocalDate dateFromChoice,
            @PathVariable LocalDate dateToChoice,
            @PathVariable Long amountOfProductsChoice,
            @PathVariable String prompt,
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
        if(prompt.equalsIgnoreCase("ALL")){
            prompt = null;
        }



        return ResponseEntity.ok(
                orderRepository.getOrderData(
                        orderStatusChoice,
                        priceFromChoice,
                        priceToChoice,
                        logic.dateConverter(dateFromChoice),
                        logic.dateConverter(dateToChoice),
                        amountOfProductsChoice,
                        prompt,
                        PageRequest.of(pageChoice, pageCountChoice)
                )
        );
    }


    @GetMapping("/getAmountOfPages/{orderStatusChoice}/{priceFromChoice}/{priceToChoice}/{dateFromChoice}/{dateToChoice}/{amountOfProductsChoice}/{prompt}")
    public ResponseEntity<Long> getAmountOfPages(
            @PathVariable  OrderStatus orderStatusChoice,
            @PathVariable Double priceFromChoice,
            @PathVariable Double priceToChoice,
            @PathVariable LocalDate dateFromChoice,
            @PathVariable LocalDate dateToChoice,
            @PathVariable Long amountOfProductsChoice,
            @PathVariable String prompt
    ) {




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
        if(prompt.equalsIgnoreCase("ALL")){
            prompt = null;
        }



        List<Long> count = orderRepository.getNumberOfOrderPages(
                orderStatusChoice,
                priceFromChoice,
                priceToChoice,
                logic.dateConverter(dateFromChoice),
                logic.dateConverter(dateToChoice),
                amountOfProductsChoice,
                prompt
        );

        Double pageCount = (double) count.size() / 5;
        Long result = (long) Math.ceil(pageCount);




        return ResponseEntity.ok(
                result
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
            sameExistingOrder.setPayMethod(order.getPayMethod());
            sameExistingOrder.setPayStatus(order.getPayStatus());







        orderRepository.save(sameExistingOrder);


        return ResponseEntity.ok(String.format("ORD-%d %s",order.getId(), "was modified and saved successfully"));
    }


    @GetMapping("/getConsumers")
    public ResponseEntity<List<ConsumerData>> getConsumerData(){
        return  ResponseEntity.ok(userRepository.getUsersExtended());
    }

    @PostMapping("/saveNewOrder")
    public ResponseEntity<String> saveNewOrder(@RequestBody Orders order){

        for(var s : order.getProductsData()){
            System.out.println(s.getProduct().getId() + "  amount " + s.getAmountOfProduct());
        }

        System.out.println("savingggggggggggg");
        System.out.println(order.getBillingAddress());

        User creator = userRepository.findById(1l).orElseThrow();

        User buyer = userRepository.findById(2l).orElseThrow();

        order.setUser(creator);
        order.setOrderPlacedBy(buyer);

        orderRepository.save(order);

        return ResponseEntity.ok("Yes");

    }









}
