package dev.com.soat.autorepairshop.infrastructure.repository.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "part_inventory_tb")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class PartInventoryEntity {

    @Id
    @Column(name = "part_id")
    private Long partId;

    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "part_id", referencedColumnName = "id")
    private PartEntity part;

    @Column(name = "quantity_on_hand", nullable = false)
    private Integer quantityOnHand;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}