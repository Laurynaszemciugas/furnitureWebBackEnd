package com.example.jwt_demo.Entity;

import com.example.jwt_demo.Enums.ActionTrackerEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
public class ActionTracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String actionName;

    @ManyToOne
    @JoinColumn(name = "who_made_it_id")
    private User whoMadeIt;



    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private ActionTrackerEnum typeOfActionRecorded;

    @CreationTimestamp
    private LocalDateTime created;

    private String name;



}
