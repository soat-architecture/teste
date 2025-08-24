package dev.com.soat.autorepairshop.infrastructure.repository;

import dev.com.soat.autorepairshop.infrastructure.repository.entity.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryEntity, Long> {

    @Modifying
    @Query(value = """
        INSERT INTO InventoryEntity
            (partId, quantityOnHand, createdAt, updatedAt)
        VALUES
            (:partId, :quantityOnHand, :createdAt, :updatedAt)
        """)
    void insertInventory(@Param("partId") Long partId,
                         @Param("quantityOnHand") Integer quantityOnHand,
                         @Param("createdAt") LocalDateTime createdAt,
                         @Param("updatedAt") LocalDateTime updatedAt);
}
