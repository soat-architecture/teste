package dev.com.soat.autorepairshop.infrastructure.repository.entity;

import dev.com.soat.autorepairshop.domain.enums.NotificationStatusType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "service_order_notifications_tb")
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class NotificationOrderEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long notificationOrderId;

    @Column(name = "service_order_id")
    private Long serviceOrderId;

    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "channel")
    private String channel;

    @Column(name = "message_content", columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private NotificationStatusType status;

    @Column(name = "sent_at")
    private LocalDateTime completedAt;
}
