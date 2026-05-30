package com.example.jwt_demo.controller;

import com.example.jwt_demo.Entity.Employee;
import com.example.jwt_demo.Entity.EmployeeJoin.OrderEmployees;
import com.example.jwt_demo.Entity.OrderJoin.OrderProducts;
import com.example.jwt_demo.Entity.Orders;
import com.example.jwt_demo.Entity.Product;
import com.example.jwt_demo.repository.EmployeeRepository;
import com.example.jwt_demo.repository.OrderRepository;
import com.example.jwt_demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/get")
    public ResponseEntity<List<Orders>> getAllOrders(){
        return ResponseEntity.ok(orderRepository.findAllFull());
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


//        System.out.println("after removing adding new data");
//        for(var s : order.getProductsData()){
//            System.out.println(s.getAmountOfProduct());
//        }



        double totalPrice = 0.0;

        for(var s : order.getProductsData()) {
            Long productId = s.getProduct().getId();
            Product existingProduct = productRepository.findById(productId).orElseThrow();

            if (s.getAmountOfProduct() <= 0 && s.getAmountOfProduct() > 101) {
                s.setAmountOfProduct(1l);
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


        return ResponseEntity.ok("Info saved");
    }







}
