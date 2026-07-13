package com.example.jwt_demo.Entity;

import com.example.jwt_demo.Common.Annotations.RequiredField;
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

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference("orders")
    @RequiredField
    private List<OrderProducts> productsData;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference("employee")
    @RequiredField
    private List<OrderEmployees> employees;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "order_placed_by_id")
    private User orderPlacedBy;

    private Double totalPrice;
    @Enumerated(EnumType.STRING)
    @RequiredField
    private OrderStatus orderStatus;
    @RequiredField
    private String orderNote;

    private String userNote;
    private String serverNote;

    @RequiredField
    private LocalDateTime estimatedDueDate;
    @RequiredField
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    @RequiredField
    private PayStatus payStatus;
    @Enumerated(EnumType.STRING)
    @RequiredField
    private PayMethod payMethod;
    @RequiredField
    private String billingAddress;

    @CreationTimestamp
    private LocalDateTime created;
    @RequiredField
    private String orderCreatedByName;
    @RequiredField
    private String orderCreatedByGmail;


}
