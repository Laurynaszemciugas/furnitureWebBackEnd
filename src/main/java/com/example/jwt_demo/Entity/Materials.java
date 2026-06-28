package com.example.jwt_demo.Entity;


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
    private String materialName;
    private Long inStock;
    private Long minThresHold;
    @Enumerated(EnumType.STRING)
    private Stock stock;
    @Enumerated(EnumType.STRING)
    private ActiveInactive enabled;
    private double materialWeight;
    private double unitPrice;

    private String unit;
    private String description;
    @Enumerated(EnumType.STRING)
    private MaterialType materialType;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String materialUrl;
    private String careInstructions;
    private String materialColor;


    @Enumerated(EnumType.STRING)
    private MaterialTextures materialTextures;

    @Enumerated(EnumType.STRING)
    private MaterialType materialFinishType;

    @Enumerated(EnumType.STRING)
    private MaterialGrainPatterns materialGrainPatterns;

    @CreationTimestamp
    private LocalDateTime created;

    @OneToMany(mappedBy = "materials",cascade = CascadeType.ALL, fetch = FetchType.LAZY , orphanRemoval = true)
    List<MaterialImageData> images = new ArrayList<>();

}
