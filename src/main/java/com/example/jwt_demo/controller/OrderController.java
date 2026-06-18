package com.example.jwt_demo.controller;

import com.example.jwt_demo.Common.ErrorResponse;
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
import com.example.jwt_demo.Enums.Warnings;
import com.example.jwt_demo.FilterDTO.Order.OrderFilterHolder;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    Map<Long,Integer> countTheTimesAccordingToUser = new HashMap<>();

    @PostMapping("/getAllOrders")
    public ResponseEntity<List<OrdersFeedData>> getAllOrders(@RequestBody OrderFilterHolder orderFilterHolder) {


        if(orderFilterHolder.getOrderStatusChoice().equals(OrderStatus.ALL)){
            orderFilterHolder.setOrderStatusChoice(null);
        }

        if(orderFilterHolder.getPriceFromChoice() == 0.0){
            orderFilterHolder.setPriceFromChoice(null);
        }
        if(orderFilterHolder.getPriceToChoice() == 0.0){
            orderFilterHolder.setPriceToChoice(null);
        }
        if(orderFilterHolder.getAmountOfProductsChoice() == 0){
            orderFilterHolder.setAmountOfProductsChoice(null);
        }
        if(orderFilterHolder.getPromptChoice().equals("ALL")){
            orderFilterHolder.setPromptChoice(null);
        }



        return ResponseEntity.ok(
                orderRepository.getOrderData(
                        orderFilterHolder.getOrderStatusChoice(),
                        orderFilterHolder.getPriceFromChoice(),
                        orderFilterHolder.getPriceToChoice(),
                        logic.dateConverter(orderFilterHolder.getDateFromChoice()),
                        logic.dateConverter(orderFilterHolder.getDateToChoice()),
                        orderFilterHolder.getAmountOfProductsChoice(),
                        orderFilterHolder.getPromptChoice(),
                        PageRequest.of(orderFilterHolder.getPage(), orderFilterHolder.getPageCount())
                )
        );
    }


    @PostMapping("/getAmountOfPages")
    public ResponseEntity<Long> getAmountOfPages(@RequestBody OrderFilterHolder orderFilterHolder) {




        if(orderFilterHolder.getOrderStatusChoice().equals(OrderStatus.ALL)){
            orderFilterHolder.setOrderStatusChoice(null);
        }

        if(orderFilterHolder.getPriceFromChoice() == 0.0){
            orderFilterHolder.setPriceFromChoice(null);
        }
        if(orderFilterHolder.getPriceToChoice() == 0.0){
            orderFilterHolder.setPriceToChoice(null);
        }
        if(orderFilterHolder.getAmountOfProductsChoice() == 0){
            orderFilterHolder.setAmountOfProductsChoice(null);
        }
        if(orderFilterHolder.getPromptChoice().equals("ALL")){
            orderFilterHolder.setPromptChoice(null);
        }


        Long count = orderRepository.getNumberOfOrderPages(
                orderFilterHolder.getOrderStatusChoice(),
                orderFilterHolder.getPriceFromChoice(),
                orderFilterHolder.getPriceToChoice(),
                logic.dateConverter(orderFilterHolder.getDateFromChoice()),
                logic.dateConverter(orderFilterHolder.getDateToChoice()),
                orderFilterHolder.getAmountOfProductsChoice(),
                orderFilterHolder.getPromptChoice()
        );






        return ResponseEntity.ok(
                count
        );
    }





    @GetMapping("/getOrderFromId/{id}")
    public ResponseEntity<Orders> getOrderFromId(@PathVariable Long id){
        return ResponseEntity.ok(orderRepository.findById(id).orElseThrow());
    }

    @PostMapping("/saveModifiedOrder")
    public ResponseEntity<ErrorResponse> saveModifiedOrder(@RequestBody Orders order){


        Orders sameExistingOrder = orderRepository.findById(order.getId()).orElseThrow();

        sameExistingOrder.getProductsData().clear();
        sameExistingOrder.getEmployees().clear();


        if(order.getBillingAddress().isEmpty() || order.getBillingAddress() == null){
            throw  new ValidationException("Address is required", Warnings.ERROR);
        }



        double totalPrice = 0.0;


        if(order.getProductsData().isEmpty() || order.getProductsData() == null){
            throw  new ValidationException("Existing order cannot be without products ", Warnings.ERROR);
        }

        for(var s : order.getProductsData()) {
            Long productId = s.getProduct().getId();
            Product existingProduct = productRepository.findById(productId).orElseThrow();

            if (s.getAmountOfProduct() <= 0 || s.getAmountOfProduct() > 101) {
                throw  new ValidationException("Product quantity can only be from 1 to 100", Warnings.ERROR);
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


        if(order.getEmployees().isEmpty() || order.getEmployees() == null){
            throw  new ValidationException("Existing order cannot be without employees ", Warnings.ERROR);
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


        return ResponseEntity.ok(new ErrorResponse(String.format("ORD-%d %s",order.getId(), "was modified and saved successfully"),Warnings.OK));
    }


    @GetMapping("/getConsumers")
    public ResponseEntity<List<ConsumerData>> getConsumerData(){
        return  ResponseEntity.ok(userRepository.getUsersExtended());
    }

    @PostMapping("/saveNewOrder")
    public ResponseEntity<ErrorResponse> saveNewOrder(@RequestBody Orders order){




        Orders newOrder = new Orders();

        newOrder.setOrderNote(order.getOrderNote());
        newOrder.setOrderStatus(order.getOrderStatus());
        newOrder.setPayMethod(order.getPayMethod());
        newOrder.setPayStatus(order.getPayStatus());
        newOrder.setBillingAddress(order.getBillingAddress());
        newOrder.setPhoneNumber(order.getPhoneNumber());
        newOrder.setEstimatedDueDate(order.getEstimatedDueDate());
        newOrder.setOrderCreatedByGmail(order.getOrderCreatedByGmail());
        newOrder.setOrderCreatedByName(order.getOrderCreatedByName());





        if(order.getProductsData() == null || order.getProductsData().isEmpty()){
            throw new ValidationException("No products are added please add products to continue", Warnings.ERROR);
        }
        else{

            Double totalPrice = 0.0;
            for(var s : order.getProductsData()){
                Product product = productRepository.findById(s.getProduct().getId()).orElseThrow();
                totalPrice+= s.getAmountOfProduct()* product.getPrice();
            }
            newOrder.setTotalPrice(totalPrice);

            List<OrderProducts> products = new ArrayList<>();
            for(var s : order.getProductsData()){

                if(s.getProduct().getId() == null){
                    throw new ValidationException("Product doesnt have an id", Warnings.FATAL_ERROR);
                }
                    Product product = productRepository.findById(s.getProduct().getId()).orElseThrow(()-> new ValidationException("Product not found", Warnings.ERROR));

                if(s.getAmountOfProduct() <= 0 || s.getAmountOfProduct() >=100){
                    throw  new ValidationException("Cannot selected that much product required from 1 to 99", Warnings.ERROR);
                }
//                if(product.getStockQuantity() < s.getAmountOfProduct()){
//                    throw new ValidationException(String.format("Order is not possible due to [%s] having less stock that is needed to fill the order | AVAILABLE STOCK %d | NEEDED STOCK %d",product.getProductName(),product.getStockQuantity(),s.getAmountOfProduct()), Warnings.ERROR);
//                }


                    OrderProducts orderProducts = new OrderProducts();
                    orderProducts.setProduct(product);
                    orderProducts.setOrder(newOrder);
                    orderProducts.setCost(totalPrice);
                    orderProducts.setAmountOfProduct(s.getAmountOfProduct());


                    products.add(orderProducts);
                }
            newOrder.setProductsData(products);
        }




        if(order.getEmployees() == null || order.getEmployees().isEmpty()){
            throw new ValidationException("No employees are selected", Warnings.ERROR);
        }
        else{
            List<OrderEmployees> employees = new ArrayList<>();
            for(var s : order.getEmployees()){

                if(s.getEmployee().getId() == null){
                    throw new ValidationException("Employee doesnt have an id", Warnings.FATAL_ERROR);
                }
                Employee employee = employeeRepository.findById(s.getEmployee().getId()).orElseThrow(()-> new ValidationException("Employee not found", Warnings.ERROR));

                OrderEmployees orderEmployees = new OrderEmployees();
                orderEmployees.setEmployee(employee);
                orderEmployees.setOrder(newOrder);


                employees.add(orderEmployees);
            }
            newOrder.setEmployees(employees);
        }


        // get creator which is admin in this case
        User creator = userRepository.findById(1l).orElseThrow();
        // if buyer not found then system cant pinpoint to whom it is needed not big deal it will be null
        User buyer = userRepository.findByGmail(order.getOrderCreatedByGmail());
        newOrder.setUser(creator);
        newOrder.setOrderPlacedBy(buyer);
        if(buyer == null){
            int times = 1;
                if (!countTheTimesAccordingToUser.isEmpty() && countTheTimesAccordingToUser.get(newOrder.getId()).equals(1)) {
                    countTheTimesAccordingToUser.remove(newOrder.getId());

                    orderRepository.save(newOrder);
                    return ResponseEntity.ok(new ErrorResponse(String.format("Order [ORD-%d] was created successfully", newOrder.getId()), Warnings.OK));
            }
            countTheTimesAccordingToUser.put(newOrder.getId(),times);
            throw new ValidationException(order.getOrderCreatedByGmail() + " is not found this is not nessasary (PRESS AGAIN TO CONFIRM) ", Warnings.WARNING);
        }




        orderRepository.save(newOrder);



        return ResponseEntity.ok(new ErrorResponse(String.format("Order [ORD-%d] was created successfully",newOrder.getId()),Warnings.OK));

    }









}
