package dev.com.soat.autorepairshop.infrastructure.gateway;

import dev.com.soat.autorepairshop.domain.entity.ServiceDomain;
import dev.com.soat.autorepairshop.infrastructure.repository.ServiceRepository;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.ServiceEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceGatewayImplTest {

    @InjectMocks
    private ServiceGatewayImpl gateway;

    @Mock
    private ServiceRepository repository;

    private ServiceDomain domain;
    private ServiceEntity entity;
    private Long serviceId;

    @BeforeEach
    void setUp() {
        serviceId = 1L;

        domain = new ServiceDomain(
                1L,
                "Troca de óleo",
                "Troca de óleo veicular",
                new BigDecimal("100.00"),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        entity = new ServiceEntity(
                1L,
                "Troca de óleo",
                "Troca de óleo veicular",
                new BigDecimal("100.00")
        );
    }

    @Test
    void testSave(){
        when(repository.save(any(ServiceEntity.class))).thenReturn(entity);

        ServiceDomain saved = gateway.save(domain);

        assertThat(saved).isNotNull();
        assertThat(saved.getIdentifier()).isEqualTo(domain.getIdentifier());
        assertThat(saved.getName()).isEqualTo(domain.getName());
        verify(repository).save(any(ServiceEntity.class));
    }

    @Test
    void testFind(){
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        ServiceDomain foundService = gateway.findById(1L);

        assertThat(foundService).isNotNull();
        assertThat(foundService.getIdentifier()).isEqualTo(1L);
        verify(repository).findById(1L);
    }

    @Test
    void testNotFound(){
        when(repository.findById(1L)).thenReturn(Optional.empty());

        ServiceDomain foundService = gateway.findById(1L);

        assertThat(foundService).isNull();
        verify(repository).findById(1L);
    }

    @Test
    void testUpdate(){
        when(repository.save(any(ServiceEntity.class))).thenReturn(entity);

        ServiceDomain updatedService = gateway.update(domain, domain);

        assertThat(updatedService).isNotNull();
        assertThat(updatedService.getIdentifier()).isEqualTo(1L);
        verify(repository).save(any(ServiceEntity.class));
    }

    @Test
    @DisplayName("Should delete a service when it exists")
    void delete_shouldDeleteServiceWhenExists() {
        // Given
        when(repository.findById(serviceId)).thenReturn(Optional.of(entity));
        doNothing().when(repository).delete(entity);

        // When
        gateway.delete(serviceId);

        // Then
        verify(repository, times(1)).findById(serviceId);
        verify(repository, times(1)).delete(entity);
    }

    @Test
    @DisplayName("Should not attempt to delete service when it does not exist")
    void delete_shouldNotDeleteServiceWhenNotExists() {
        // Given
        when(repository.findById(serviceId)).thenReturn(Optional.empty());

        // When
        gateway.delete(serviceId);

        // Then
        verify(repository, times(1)).findById(serviceId);
        verify(repository, never()).delete(any(ServiceEntity.class));
    }
}
