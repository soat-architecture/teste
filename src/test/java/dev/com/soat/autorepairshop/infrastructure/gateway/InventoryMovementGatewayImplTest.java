package dev.com.soat.autorepairshop.infrastructure.gateway;

import dev.com.soat.autorepairshop.domain.entity.InventoryMovementDomain;
import dev.com.soat.autorepairshop.infrastructure.api.mapper.InventoryMovementMapper;
import dev.com.soat.autorepairshop.infrastructure.repository.InventoryMovementRepository;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.InventoryMovementEntity;
import dev.com.soat.autorepairshop.domain.enums.MovementType;

import dev.com.soat.autorepairshop.infrastructure.repository.mapper.InventoryMovementEntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryMovementGatewayImplTest {

    @Mock
    private InventoryMovementRepository repository;

    @InjectMocks
    private InventoryMovementGatewayImpl inventoryMovementGateway;

    private InventoryMovementDomain movementDomain;
    private InventoryMovementEntity movementEntity;
    private Long partId;
    private LocalDateTime now;

    // Static mocks for the mappers
    private MockedStatic<InventoryMovementEntityMapper> mockedInventoryMovementEntityMapper;
    private MockedStatic<InventoryMovementMapper> mockedInventoryMovementMapper;

    @BeforeEach
    void setUp() {
        partId = 1L;
        now = LocalDateTime.now().withNano(0); // Normalize nanoseconds

        movementDomain = new InventoryMovementDomain(
                10L, // movement ID
                partId,
                null,
                null,
                MovementType.INBOUND,
                50,
                100,
                150,
                "Test inbound movement",
                now.minusDays(1)
        );

        movementEntity = new InventoryMovementEntity(
                10L,
                partId,
                null,
                null,
                MovementType.INBOUND,
                50,
                100,
                150,
                "Test inbound movement",
                now.minusDays(1)
        );

        // Initialize all static mocks
        mockedInventoryMovementEntityMapper = mockStatic(InventoryMovementEntityMapper.class);
        mockedInventoryMovementMapper = mockStatic(InventoryMovementMapper.class);
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        // Close all static mocks
        if (mockedInventoryMovementEntityMapper != null) mockedInventoryMovementEntityMapper.close();
        if (mockedInventoryMovementMapper != null) mockedInventoryMovementMapper.close();
    }

    // --- Tests for save(InventoryMovementDomain domain) ---

    @Test
    @DisplayName("Should successfully save an inventory movement")
    void save_shouldSaveMovementSuccessfully() {
        // Given
        mockedInventoryMovementEntityMapper.when(() -> InventoryMovementEntityMapper.map(movementDomain))
                .thenReturn(movementEntity);

        // FIX APPLIED HERE: Change doNothing() to when().thenReturn()
        when(repository.save(movementEntity)).thenReturn(movementEntity); // Assuming save returns the saved entity

        // When
        assertDoesNotThrow(() -> inventoryMovementGateway.save(movementDomain));

        // Then
        mockedInventoryMovementEntityMapper.verify(() -> InventoryMovementEntityMapper.map(movementDomain), times(1));
        verify(repository, times(1)).save(movementEntity);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when input domain is null during save due to repository")
    void save_shouldThrowIllegalArgumentExceptionForNullInput() {
        // Given
        InventoryMovementDomain nullDomain = null;
        // Assume InventoryMovementEntityMapper.map(null) returns null
        mockedInventoryMovementEntityMapper.when(() -> InventoryMovementEntityMapper.map(isNull(InventoryMovementDomain.class)))
                .thenReturn(null);
        // And repository.save(null) throws IllegalArgumentException
        when(repository.save(isNull(InventoryMovementEntity.class))).thenThrow(new IllegalArgumentException("Entity must not be null"));

        // When / Then
        // Expecting IllegalArgumentException from repository.save(null)
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            inventoryMovementGateway.save(nullDomain);
        });

        assertThat(thrown.getMessage()).contains("Entity must not be null");

        // Verify that mapper was called with null, and repository save was called with null (and threw)
        mockedInventoryMovementEntityMapper.verify(() -> InventoryMovementEntityMapper.map(isNull(InventoryMovementDomain.class)), times(1));
        verify(repository, times(1)).save(isNull(InventoryMovementEntity.class));
    }

    @Test
    @DisplayName("Should propagate exception if InventoryMovementEntityMapper.map throws exception during save")
    void save_shouldPropagateExceptionFromEntityMapper() {
        // Given
        RuntimeException mapperException = new RuntimeException("Entity mapping error");
        mockedInventoryMovementEntityMapper.when(() -> InventoryMovementEntityMapper.map(movementDomain))
                .thenThrow(mapperException);

        // When / Then
        assertThatThrownBy(() -> inventoryMovementGateway.save(movementDomain))
                .isInstanceOf(RuntimeException.class)
                .isEqualTo(mapperException);

        mockedInventoryMovementEntityMapper.verify(() -> InventoryMovementEntityMapper.map(movementDomain), times(1));
        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("Should propagate exception if repository.save throws exception during save")
    void save_shouldPropagateExceptionFromRepositorySave() {
        // Given
        mockedInventoryMovementEntityMapper.when(() -> InventoryMovementEntityMapper.map(movementDomain))
                .thenReturn(movementEntity);
        RuntimeException repoException = new RuntimeException("DB save error");
        // FIX APPLIED HERE: Change doThrow(repoException).when().save() to when().thenReturn().thenThrow() if save returns
        // Or directly when().thenThrow() if it's the only behavior
        when(repository.save(movementEntity)).thenThrow(repoException);

        // When / Then
        assertThatThrownBy(() -> inventoryMovementGateway.save(movementDomain))
                .isInstanceOf(RuntimeException.class)
                .isEqualTo(repoException);

        mockedInventoryMovementEntityMapper.verify(() -> InventoryMovementEntityMapper.map(movementDomain), times(1));
        verify(repository, times(1)).save(movementEntity);
    }

    // --- Tests for findByPartId(Long partId) ---

    @Test
    @DisplayName("Should return a list of InventoryMovementDomain when movements are found by partId")
    void findByPartId_shouldReturnListOfDomainsWhenFound() {
        // Given
        InventoryMovementEntity entity1 = new InventoryMovementEntity(
                1L, partId, null, null, MovementType.INBOUND, 10, 0, 10, "Mov 1", now.minusMinutes(10)
        );
        InventoryMovementEntity entity2 = new InventoryMovementEntity(
                2L, partId, null, null, MovementType.ADJUSTMENT, 5, 10, 5, "Mov 2", now.minusMinutes(5)
        );

        InventoryMovementDomain domain1 = new InventoryMovementDomain(
                1L, partId, null, null, MovementType.INBOUND, 10, 0, 10, "Mov 1", now.minusMinutes(10)
        );
        InventoryMovementDomain domain2 = new InventoryMovementDomain(
                2L, partId, null, null, MovementType.ADJUSTMENT, 5, 10, 5, "Mov 2", now.minusMinutes(5)
        );

        List<InventoryMovementEntity> foundEntities = Arrays.asList(entity1, entity2);

        when(repository.findAllByPartId(partId)).thenReturn(foundEntities);
        mockedInventoryMovementMapper.when(() -> InventoryMovementMapper.map(entity1)).thenReturn(domain1);
        mockedInventoryMovementMapper.when(() -> InventoryMovementMapper.map(entity2)).thenReturn(domain2);

        // When
        List<InventoryMovementDomain> result = inventoryMovementGateway.findByPartId(partId);

        // Then
        assertThat(result)
                .isNotNull()
                .hasSize(2)
                .containsExactly(domain1, domain2);

        verify(repository, times(1)).findAllByPartId(partId);
        mockedInventoryMovementMapper.verify(() -> InventoryMovementMapper.map(entity1), times(1));
        mockedInventoryMovementMapper.verify(() -> InventoryMovementMapper.map(entity2), times(1));
    }

    @Test
    @DisplayName("Should return an empty list when no movements are found by partId")
    void findByPartId_shouldReturnEmptyListWhenNotFound() {
        // Given
        when(repository.findAllByPartId(partId)).thenReturn(Collections.emptyList());

        // When
        List<InventoryMovementDomain> result = inventoryMovementGateway.findByPartId(partId);

        // Then
        assertThat(result)
                .isNotNull()
                .isEmpty();

        verify(repository, times(1)).findAllByPartId(partId);
        mockedInventoryMovementMapper.verify(() -> InventoryMovementMapper.map(any(InventoryMovementEntity.class)), never());
    }

    @Test
    @DisplayName("Should return an empty list when repository.findAllByPartid returns null (though typically it returns empty list)")
    void findByPartId_shouldReturnEmptyListWhenRepositoryReturnsNull() {
        // Given
        when(repository.findAllByPartId(partId)).thenReturn(null);

        // When
        List<InventoryMovementDomain> result = inventoryMovementGateway.findByPartId(partId);

        // Then
        assertThat(result)
                .isNotNull()
                .isEmpty();

        verify(repository, times(1)).findAllByPartId(partId);
        mockedInventoryMovementMapper.verify(() -> InventoryMovementMapper.map(any(InventoryMovementEntity.class)), never());
    }

    @Test
    @DisplayName("Should propagate exception if repository.findAllByPartid throws an exception")
    void findByPartId_shouldPropagateExceptionFromRepository() {
        // Given
        RuntimeException repoException = new RuntimeException("Database error during find all by partId");
        when(repository.findAllByPartId(partId)).thenThrow(repoException);

        // When / Then
        assertThatThrownBy(() -> inventoryMovementGateway.findByPartId(partId))
                .isInstanceOf(RuntimeException.class)
                .isEqualTo(repoException);

        verify(repository, times(1)).findAllByPartId(partId);
        mockedInventoryMovementMapper.verifyNoInteractions();
    }

    @Test
    @DisplayName("Should propagate NullPointerException when partId is null (from repository)")
    void findByPartId_shouldPropagateNullPointerExceptionWhenPartIdIsNull() {
        // Given
        Long nullPartId = null;
        when(repository.findAllByPartId(nullPartId)).thenThrow(new NullPointerException("partId cannot be null"));

        // When / Then
        assertThatThrownBy(() -> inventoryMovementGateway.findByPartId(nullPartId))
                .isInstanceOf(NullPointerException.class);

        verify(repository, times(1)).findAllByPartId(nullPartId);
        mockedInventoryMovementMapper.verifyNoInteractions();
    }
}