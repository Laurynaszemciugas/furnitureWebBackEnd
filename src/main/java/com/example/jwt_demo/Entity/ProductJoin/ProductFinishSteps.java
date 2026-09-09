package com.example.jwt_demo.Entity.ProductJoin;


import com.example.jwt_demo.Entity.Product;
import com.example.jwt_demo.Entity.User;
import com.example.jwt_demo.Enums.ProductFinishStepStatus;
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
    private Long id = null;
    private Long stepId = null;
    private String stepName = null;
    private String stepDescription = null;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonIgnore
    private Product product = null;

    @Enumerated(EnumType.STRING)
    private ProductFinishStepStatus productFinishStepStatus;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private User employee;


}
