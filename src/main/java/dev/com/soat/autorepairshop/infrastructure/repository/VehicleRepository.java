package dev.com.soat.autorepairshop.infrastructure.repository;

import dev.com.soat.autorepairshop.infrastructure.repository.entity.VehicleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface VehicleRepository extends JpaRepository<VehicleEntity, Long> {
    @Query("SELECT v FROM VehicleEntity v WHERE v.licensePlate = :licensePlate")
    VehicleEntity findVehicleByLicensePlate(@Param("licensePlate") String licensePlate);

    @Query("SELECT v FROM VehicleEntity v WHERE v.document = :document")
    List<VehicleEntity> findVehicleByOwner(@Param("document") String document);

    @Modifying
    @Transactional
    @Query(value = "UPDATE VehicleEntity SET active = false WHERE licensePlate = :licensePlate")
    void softDeleteByLicensePlate(@Param("licensePlate") String licensePlate);
}
