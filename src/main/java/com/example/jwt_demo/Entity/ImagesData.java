package com.example.jwt_demo.Entity;

import com.example.jwt_demo.Enums.ImageLogic;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@Table(name = "imagedData")
public class ImagesData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String uuId;
    private String imageName;
    private String imageUrl;
    private String imageType;
    @Enumerated(EnumType.STRING)
    private ImageLogic imageLogic;

    @Lob
    private byte[] imageData;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonIgnoreProperties("product")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @CreationTimestamp
    private LocalDateTime created;

}
