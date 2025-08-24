package dev.com.soat.autorepairshop.infrastructure.repository.entity;

import dev.com.soat.autorepairshop.domain.enums.OrderStatusType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "service_order_status_history_tb")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OrderHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long identifier;

    @Column(name = "service_order_id", nullable = false)
    private Long orderId;

    @Column(nullable = false, length = 100)
    @Enumerated(EnumType.STRING)
    private OrderStatusType status;

    @Column(nullable = false, length = 400)
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
