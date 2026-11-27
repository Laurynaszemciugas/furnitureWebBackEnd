package com.example.jwt_demo.Entity;

import com.example.jwt_demo.Enums.NotificationTypes;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Notifications {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String notificationImage;
    private String notificationName;
    private String message;
    private String messageHighLightedText;
    @Enumerated(EnumType.STRING)
    private NotificationTypes notificationType;

    // this can be person material order product ect ect just a refrence point so later could get the full item from rep
    private Long itemId;

    private boolean readedNotification;
    @CreationTimestamp
    private LocalDateTime created;

    @ManyToOne
    @JoinColumn(name = "sender")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver")
    private User receiver;

}
