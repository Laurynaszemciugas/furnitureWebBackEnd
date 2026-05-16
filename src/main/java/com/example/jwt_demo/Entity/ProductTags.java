package com.example.jwt_demo.Entity;


import com.example.jwt_demo.Enums.Tags;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "productTags")
public class ProductTags {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonIgnore
    private Product product;

    @Enumerated(EnumType.STRING)
    private Tags tags;

    @ManyToOne
    @JoinColumn(name = "user_id")

    private User user;

    @CreationTimestamp
    private LocalDateTime created;

}
