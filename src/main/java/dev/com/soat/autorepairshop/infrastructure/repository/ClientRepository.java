package dev.com.soat.autorepairshop.infrastructure.repository;

import dev.com.soat.autorepairshop.infrastructure.repository.entity.ClientEntity;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, Long> {

    ClientEntity findByDocument(String document);

    @Modifying
    @Transactional
    @Query(value = "UPDATE autorepairshop.clients_tb SET status = 'DELETED' WHERE document = :document", nativeQuery = true)
    void softDeleteByDocument(@Param("document") String document);

    @QueryHints(value = {
        @QueryHint(name = "jakarta.persistence.query.timeout", value = "3000"),
        @QueryHint(name = "org.hibernate.readOnly", value = "true")
    })
    @Query(value = "SELECT * FROM autorepairshop.clients_tb WHERE document = :document OR email = :email OR phone = :phone", nativeQuery = true)
    ClientEntity findByConstraints(String document, String email, String phone);

}
