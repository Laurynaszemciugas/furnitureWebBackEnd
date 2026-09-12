package com.example.jwt_demo.Entity.OrderJoin;

import com.example.jwt_demo.Entity.Orders;
import com.example.jwt_demo.Entity.Product;
import com.example.jwt_demo.Entity.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
public class OrderProducts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "product_id")

    private Product product;
    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference("orders")
    private Orders order;
    private Long amountOfProduct;
    private Double cost;


    @OneToMany(mappedBy = "orderProducts",cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference("orderSteps")
    private List<OrderStepsToComplete> orderSteps;




    @CreationTimestamp
    private LocalDateTime created;

}
