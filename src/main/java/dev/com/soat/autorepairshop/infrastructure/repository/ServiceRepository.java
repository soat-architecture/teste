package dev.com.soat.autorepairshop.infrastructure.repository;

import dev.com.soat.autorepairshop.infrastructure.repository.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {
}
