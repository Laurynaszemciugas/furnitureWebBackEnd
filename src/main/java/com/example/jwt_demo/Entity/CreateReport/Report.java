package com.example.jwt_demo.Entity.CreateReport;

import com.example.jwt_demo.Entity.User;
import com.example.jwt_demo.Enums.ReportCategory;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reportName;
    private String reportColor;
    private String description;

    @Enumerated(EnumType.STRING)
    private ReportCategory reportCategory;

    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "report",cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ReportItems> reportItemsList;

}
