package com.example.jwt_demo.Entity;


import com.example.jwt_demo.Entity.ProductJoin.ProductMaterials;
import com.example.jwt_demo.Enums.Category;
import com.example.jwt_demo.Enums.Status;
import com.example.jwt_demo.Enums.Visibility;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String productName;
    private String sku;
    private String description;
    private double price;
    private double discount;
    private double materialCost;
    private Long stockQuantity;
    private Long lowStockThreshold;
    @Enumerated(EnumType.STRING)
    private Category category;
    @Enumerated(EnumType.STRING)
    private Status status;
    @Enumerated(EnumType.STRING)
    private Visibility visibility;

    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<ProductTags> tags = new ArrayList<>();

    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<ImagesData> images = new ArrayList<>();

    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<ProductMaterials> materials = new ArrayList<>();

    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<ExtraDetails> extraDetails = new ArrayList<>();

    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<ProductComments> comments= new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @CreationTimestamp
    private LocalDateTime created;



}
