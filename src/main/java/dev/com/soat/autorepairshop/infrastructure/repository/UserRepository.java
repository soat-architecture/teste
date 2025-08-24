package dev.com.soat.autorepairshop.infrastructure.repository;

import dev.com.soat.autorepairshop.infrastructure.repository.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByDocument(String document);

    @Query("SELECT u FROM UserEntity u WHERE u.userId = ?1")
    Optional<UserEntity> findEmployeeById(Long id);
}
