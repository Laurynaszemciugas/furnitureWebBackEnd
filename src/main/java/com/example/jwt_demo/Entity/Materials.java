package com.example.jwt_demo.Entity;


import com.example.jwt_demo.Common.Annotations.RequiredField;
import com.example.jwt_demo.Enums.*;
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
@ToString
@Entity
@Table(name = "materials")
public class Materials {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @RequiredField
    private String materialName;
    @RequiredField
    private Long inStock;
    @RequiredField
    private Long minThresHold;
    @Enumerated(EnumType.STRING)
    private Stock stock;
    @Enumerated(EnumType.STRING)
    @RequiredField
    private ActiveInactive enabled;
    @RequiredField
    private double materialWeight;
    @RequiredField
    private double unitPrice;

    @RequiredField
    private String unit;
    @RequiredField
    private String description;
    @Enumerated(EnumType.STRING)
    @RequiredField
    private MaterialType materialType;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String materialUrl;
    @RequiredField
    private String careInstructions;
    @RequiredField
    private String materialColor;


    @Enumerated(EnumType.STRING)
    @RequiredField
    private MaterialTextures materialTextures;

    @Enumerated(EnumType.STRING)
    @RequiredField
    private MaterialType materialFinishType;

    @Enumerated(EnumType.STRING)
    @RequiredField
    private MaterialGrainPatterns materialGrainPatterns;

    @CreationTimestamp
    private LocalDateTime created;

    @OneToMany(mappedBy = "materials",cascade = CascadeType.ALL, fetch = FetchType.LAZY , orphanRemoval = true)
    List<MaterialImageData> images = new ArrayList<>();

}
