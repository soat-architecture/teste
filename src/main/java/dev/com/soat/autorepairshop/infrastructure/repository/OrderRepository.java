package dev.com.soat.autorepairshop.infrastructure.repository;

import dev.com.soat.autorepairshop.domain.enums.OrderStatusType;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    @Query("SELECT o FROM OrderEntity o WHERE o.vehicle.licensePlate = :vehicleLicensePlate AND o.completedAt IS NULL")
    OrderEntity findOrderActiveByVehicleLicensePlate(@Param("vehicleLicensePlate") String vehicleLicensePlate);

    @Query("SELECT o FROM OrderEntity o WHERE o.status IN (:status) AND o.completedAt IS NOT NULL")
    List<OrderEntity> findAllByStatus(List<OrderStatusType> status);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE service_orders_tb SET active_budget_id =:budgetId WHERE id =:orderId", nativeQuery = true)
    int updateActiveBudget(@Param("orderId") Long orderId, @Param("budgetId") Long budgetId);
}