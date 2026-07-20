package com.example.jwt_demo.Entity;

import com.example.jwt_demo.Enums.Type;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Type type;

    private Long amountTakeAdd;

    private Long balance;

    @ManyToOne
    @JoinColumn(name = "materials_id")
    private Materials materials;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @CreationTimestamp
    private LocalDateTime created;



}
