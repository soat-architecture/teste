package dev.com.soat.autorepairshop.infrastructure.repository;

import dev.com.soat.autorepairshop.infrastructure.repository.entity.BudgetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<BudgetEntity, Long> {


    long countByServiceOrderId(Long serviceOrderId);

    List<BudgetEntity> findByServiceOrderIdOrderByVersionDesc(Long serviceOrderId);

    @Query("SELECT COALESCE(MAX(b.version), 0) " +
            "FROM BudgetEntity b " +
            "WHERE b.serviceOrderId = :serviceOrderId")
    int findMaxVersionByServiceOrderId(@Param("serviceOrderId") Long serviceOrderId);
}
