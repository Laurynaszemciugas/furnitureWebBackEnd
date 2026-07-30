package com.example.jwt_demo.Entity.CreateReport;

import com.example.jwt_demo.Enums.Widget;
import com.example.jwt_demo.Enums.Widths;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
public class ReportItems {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customId;

    @Enumerated(EnumType.STRING)
    private Widget widget;

    @Enumerated(EnumType.STRING)
    private Widths Width;

    @ManyToOne
    @JoinColumn(name = "report_id")
    private Report report;

}
