package com.example.jwt_demo.Entity;


import com.example.jwt_demo.Enums.Enabled;
import com.example.jwt_demo.Enums.Stock;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.CurrentTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private Enabled enabled;
    private double materialWeight;
    private double unitPrice;
    private String unit;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @CreationTimestamp
    private LocalDateTime created;

}
