package com.example.jwt_demo.Entity;

import com.example.jwt_demo.Enums.AccountStatus;
import com.example.jwt_demo.Enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.CurrentTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String gmail;
    private String name;
    private String lastName;
    private String password;
    private String recoveryPin;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;
    private LocalDate bannedTill;
    @CreationTimestamp
    private LocalDateTime created;

    private String fullName;
    @Lob
    private String imageUrl;



}
