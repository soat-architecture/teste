package dev.com.soat.autorepairshop.infrastructure.repository;

import dev.com.soat.autorepairshop.infrastructure.repository.entity.PartInventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PartInventoryRepository extends JpaRepository<PartInventoryEntity, Long> {

    @Query("SELECT i.quantityOnHand FROM PartInventoryEntity i WHERE i.partId = :partId")
    Integer findOnHand(@Param("partId") Long partId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE PartInventoryEntity i
        SET i.quantityOnHand = i.quantityOnHand - :qty,
            i.updatedAt = CURRENT_TIMESTAMP
        WHERE i.partId = :partId AND i.quantityOnHand >= :qty
        """)
    int tryDecrease(@Param("partId") Long partId, @Param("qty") int qty);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE PartInventoryEntity i
        SET i.quantityOnHand = i.quantityOnHand + :qty,
            i.updatedAt = CURRENT_TIMESTAMP
        WHERE i.partId = :partId
        """)
    int tryIncrease(@Param("partId") Long partId, @Param("qty") int qty);
}
