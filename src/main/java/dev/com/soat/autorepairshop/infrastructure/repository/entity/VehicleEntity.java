package dev.com.soat.autorepairshop.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vehicles_tb", uniqueConstraints = {
    @UniqueConstraint(name = "uk_license_plate_owner", columnNames = { "licensePlate", "document" })
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class VehicleEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "license_plate", nullable = false, length = 8)
    private String licensePlate;

    @Column(nullable = false, length = 100)
    private String brand;

    @Column(nullable = false, length = 100)
    private String model;

    @Column(nullable = false)
    private Integer manufactureYear;

    @Column(nullable = false, length = 5)
    private String vehicleType;

    @Column(length = 100)
    private String carBodyType;

    @Column(length = 100)
    private String motorcycleStyleType;

    @Column(nullable = false, length = 100)
    private String color;

    @Column(nullable = false, length = 18)
    private String document;

    @Column(nullable = false)
    private boolean active;

}
