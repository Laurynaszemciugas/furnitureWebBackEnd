package com.example.jwt_demo.Entity;

import com.example.jwt_demo.Entity.EmployeeJoin.OrderEmployees;
import com.example.jwt_demo.Entity.OrderJoin.OrderProducts;
import com.example.jwt_demo.Enums.OrderStatus;
import com.example.jwt_demo.Enums.PayMethod;
import com.example.jwt_demo.Enums.PayStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonManagedReference("orders")
    private List<OrderProducts> productsData;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonManagedReference("employee")
    private List<OrderEmployees> employees;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "order_placed_by_id")
    private User orderPlacedBy;

    private Double totalPrice;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    private String orderNote;
    private LocalDateTime estimatedDueDate;
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    private PayStatus payStatus;
    @Enumerated(EnumType.STRING)
    private PayMethod payMethod;
    private String billingAddress;

    @CreationTimestamp
    private LocalDateTime created;


}
