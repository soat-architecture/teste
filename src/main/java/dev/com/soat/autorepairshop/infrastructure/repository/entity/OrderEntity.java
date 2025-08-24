package dev.com.soat.autorepairshop.infrastructure.repository.entity;

import dev.com.soat.autorepairshop.domain.enums.OrderStatusType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "service_orders_tb")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OrderEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long identifier;

    @ManyToOne
    @JoinColumn(name = "client_document", referencedColumnName = "document", nullable = false)
    private ClientEntity client;

    @ManyToOne
    @JoinColumn(name = "vehicle_license_plate", referencedColumnName = "license_plate", nullable = false)
    private VehicleEntity vehicle;

    @OneToOne
    @JoinColumn(name = "active_budget_id", referencedColumnName = "id")
    private BudgetEntity budget;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatusType status;

    @Column(nullable = false, length = 400)
    private String notes;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "service_id", nullable = false)
    private Long serviceId;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

}