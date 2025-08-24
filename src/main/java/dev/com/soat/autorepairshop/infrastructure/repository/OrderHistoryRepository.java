package dev.com.soat.autorepairshop.infrastructure.repository;

import dev.com.soat.autorepairshop.infrastructure.repository.entity.OrderHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderHistoryRepository extends JpaRepository<OrderHistoryEntity, Long> {

    List<OrderHistoryEntity> findAllByOrderId(Long orderId);
}
