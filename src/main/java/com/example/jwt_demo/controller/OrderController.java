package com.example.jwt_demo.controller;

import com.example.jwt_demo.Entity.Orders;
import com.example.jwt_demo.Entity.Product;
import com.example.jwt_demo.repository.OrderRepository;
import com.example.jwt_demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ProductRepository productRepository;

    @GetMapping("/get")
    public ResponseEntity<List<Orders>> getAllOrders(){

        List<Product> products = productRepository.findAll();
        System.out.println( "ssssssssssssssssssssssssssssssssssssssssssssssss "+ products);
        for(var s : products){
            System.out.println(s.getProductName());
            for(var mats : s.getMaterials()){
                System.out.println(mats.getNameForRefrence());
            }
        }
        System.out.println( "ssssssssssssssssssssssssssssssssssssssssssssssss "+ products);


        return ResponseEntity.ok(orderRepository.findAllFull());
    }

    @GetMapping("/getP")
    public ResponseEntity<List<Product>> sss(){

        List<Product> products = productRepository.findAll();
        System.out.println( "ssssssssssssssssssssssssssssssssssssssssssssssss "+ products);
        for(var s : products){
            System.out.println(s.getProductName());
            for(var mats : s.getMaterials()){
                System.out.println(mats.getNameForRefrence());
            }
        }
        System.out.println( "ssssssssssssssssssssssssssssssssssssssssssssssss "+ products);


        return ResponseEntity.ok(productRepository.findAll());
    }


}
