package com.example.jwt_demo.Entity.ProductJoin;


import com.example.jwt_demo.Entity.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ProductFinishSteps {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long step;
    private String stepName;
    private String stepDescription;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonIgnore
    private Product product;



}
