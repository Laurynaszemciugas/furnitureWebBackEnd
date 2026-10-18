package com.example.jwt_demo.Entity;

import com.example.jwt_demo.Entity.ProductJoin.ProductFinishSteps;
import com.example.jwt_demo.Entity.ProductJoin.ProductMaterials;
import com.example.jwt_demo.Enums.Category;
import com.example.jwt_demo.Enums.Status;
import com.example.jwt_demo.Enums.Stock;
import com.example.jwt_demo.Enums.Visibility;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity

public class ProductMultiple {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String color;

    private boolean stockCalculatedManually;

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

    @Enumerated(EnumType.STRING)
    private Stock stock;


    @OneToMany(
            mappedBy = "productMultiple",
            cascade = CascadeType.ALL
    )
    private List<ProductImageData> images = new ArrayList<>();

    @OneToMany(
            mappedBy = "productMultiple",
            cascade = CascadeType.ALL
    )
    private List<ProductMaterials> materials = new ArrayList<>();


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    // link all new multiple products to the product
    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonBackReference("multipleProductTypes")
    private Product product;

    @CreationTimestamp
    private LocalDateTime created;


}
