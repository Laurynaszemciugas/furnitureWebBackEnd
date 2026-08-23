package com.example.jwt_demo.Entity.CreateReport;

import com.example.jwt_demo.Enums.Widget;
import com.example.jwt_demo.Enums.Widths;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ReportItems {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customId;

    @Enumerated(EnumType.STRING)
    private Widget widget;

    @Enumerated(EnumType.STRING)
    private Widths width;

    private String userPreferredWidth;

    @ManyToOne
    @JoinColumn(name = "report_id")
    @JsonBackReference("reportRef")
    private Report report;

}
