package dev.com.soat.autorepairshop.infrastructure.repository;

import dev.com.soat.autorepairshop.infrastructure.repository.entity.NotificationOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationOrderRepository extends JpaRepository<NotificationOrderEntity, Long> {
}
