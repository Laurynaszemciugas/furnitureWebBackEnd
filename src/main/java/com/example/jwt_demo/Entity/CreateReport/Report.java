package com.example.jwt_demo.Entity.CreateReport;

import com.example.jwt_demo.Entity.User;
import com.example.jwt_demo.Enums.DashboardWidget;
import com.example.jwt_demo.Enums.ReportCategory;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
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

    @Enumerated(EnumType.STRING)
    private DashboardWidget dashboardWidget;

    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "report",cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference("reportRef")
    private List<ReportItems> reportItemsList;

    @CreationTimestamp
    private LocalDateTime created;

}
