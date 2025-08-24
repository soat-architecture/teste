package dev.com.soat.autorepairshop.infrastructure.gateway;

import dev.com.soat.autorepairshop.domain.entity.InventoryDomain;
import dev.com.soat.autorepairshop.infrastructure.api.mapper.InventoryMapper;
import dev.com.soat.autorepairshop.infrastructure.repository.InventoryRepository;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.InventoryEntity;
import dev.com.soat.autorepairshop.infrastructure.repository.mapper.InventoryEntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryGatewayImplTest {

    @Mock
    private InventoryRepository repository;

    @InjectMocks
    private InventoryGatewayImpl inventoryGateway; // The class under test

    private Long partId;
    private InventoryDomain inventoryDomain; // Represents a domain object for input/output
    private InventoryEntity inventoryEntity; // Represents an entity for repository interaction
    private LocalDateTime fixedNow;

    // Static mocks for the mappers and LocalDateTime
    private MockedStatic<InventoryEntityMapper> mockedInventoryEntityMapper;
    private MockedStatic<InventoryMapper> mockedInventoryMapper;
    private MockedStatic<LocalDateTime> mockedLocalDateTime;


    @BeforeEach
    void setUp() {
        partId = 1L;
        fixedNow = LocalDateTime.of(2025, 7, 24, 10, 0, 0); // Control LocalDateTime.now()

        inventoryDomain = new InventoryDomain(
                partId,
                100,
                fixedNow.minusDays(5),
                fixedNow.minusHours(1)
        );

        inventoryEntity = new InventoryEntity(); // Assuming default constructor and setters
        inventoryEntity.setPartId(partId);
        inventoryEntity.setQuantityOnHand(100);
        inventoryEntity.setCreatedAt(fixedNow.minusDays(5));
        inventoryEntity.setUpdatedAt(fixedNow.minusHours(1));

        // Initialize all static mocks
        mockedInventoryEntityMapper = mockStatic(InventoryEntityMapper.class);
        mockedInventoryMapper = mockStatic(InventoryMapper.class);
        mockedLocalDateTime = mockStatic(LocalDateTime.class);
        mockedLocalDateTime.when(LocalDateTime::now).thenReturn(fixedNow); // Fix LocalDateTime.now()
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        // Close all static mocks
        if (mockedInventoryEntityMapper != null) mockedInventoryEntityMapper.close();
        if (mockedInventoryMapper != null) mockedInventoryMapper.close();
        if (mockedLocalDateTime != null) mockedLocalDateTime.close();
    }

    // --- Test for save(InventoryDomain inventory) ---
    @Test
    @DisplayName("Should successfully save a new inventory item")
    void save_shouldSaveInventorySuccessfully() {
        // Given
        // 1. InventoryDomain maps to InventoryEntity (null partId for new)
        mockedInventoryEntityMapper.when(() -> InventoryEntityMapper.map(inventoryDomain))
                .thenReturn(inventoryEntity);
        // 2. Repository saves the entity and returns the saved entity (often the same instance with ID)
        when(repository.save(inventoryEntity)).thenReturn(inventoryEntity);
        // 3. Saved InventoryEntity maps back to InventoryDomain
        mockedInventoryMapper.when(() -> InventoryMapper.map(inventoryEntity)).thenReturn(inventoryDomain);

        // When
        InventoryDomain saved = inventoryGateway.save(inventoryDomain);

        // Then
        assertThat(saved).isEqualTo(inventoryDomain); // Verify the returned domain object
        mockedInventoryEntityMapper.verify(() -> InventoryEntityMapper.map(inventoryDomain), times(1));
        verify(repository, times(1)).save(inventoryEntity);
        mockedInventoryMapper.verify(() -> InventoryMapper.map(inventoryEntity), times(1));
    }

    @Test
    @DisplayName("Should propagate exception if InventoryEntityMapper.map throws exception during save")
    void save_shouldPropagateExceptionFromEntityMapper() {
        // Given
        RuntimeException mapperException = new RuntimeException("Entity mapping error");
        mockedInventoryEntityMapper.when(() -> InventoryEntityMapper.map(inventoryDomain))
                .thenThrow(mapperException);

        // When / Then
        assertThatThrownBy(() -> inventoryGateway.save(inventoryDomain))
                .isInstanceOf(RuntimeException.class)
                .isEqualTo(mapperException);

        mockedInventoryEntityMapper.verify(() -> InventoryEntityMapper.map(inventoryDomain), times(1));
        verifyNoInteractions(repository);
        mockedInventoryMapper.verifyNoInteractions();
    }

    @Test
    @DisplayName("Should propagate exception if repository.save throws exception during save")
    void save_shouldPropagateExceptionFromRepositorySave() {
        // Given
        mockedInventoryEntityMapper.when(() -> InventoryEntityMapper.map(inventoryDomain))
                .thenReturn(inventoryEntity);
        RuntimeException repoException = new RuntimeException("DB save error");
        when(repository.save(inventoryEntity)).thenThrow(repoException);

        // When / Then
        assertThatThrownBy(() -> inventoryGateway.save(inventoryDomain))
                .isInstanceOf(RuntimeException.class)
                .isEqualTo(repoException);

        mockedInventoryEntityMapper.verify(() -> InventoryEntityMapper.map(inventoryDomain), times(1));
        verify(repository, times(1)).save(inventoryEntity);
        mockedInventoryMapper.verifyNoInteractions();
    }

    // --- Test for findById(Long partId) ---
    @Test
    @DisplayName("Should return InventoryDomain when inventory is found by ID")
    void findById_shouldReturnDomainWhenFound() {
        // Given
        when(repository.findById(partId)).thenReturn(Optional.of(inventoryEntity));
        mockedInventoryMapper.when(() -> InventoryMapper.map(inventoryEntity)).thenReturn(inventoryDomain);

        // When
        InventoryDomain found = inventoryGateway.findById(partId);

        // Then
        assertThat(found).isEqualTo(inventoryDomain);
        verify(repository, times(1)).findById(partId);
        mockedInventoryMapper.verify(() -> InventoryMapper.map(inventoryEntity), times(1));
    }

    @Test
    @DisplayName("Should return null when inventory is not found by ID")
    void findById_shouldReturnNullWhenNotFound() {
        // Given
        when(repository.findById(partId)).thenReturn(Optional.empty());

        // When
        InventoryDomain found = inventoryGateway.findById(partId);

        // Then
        assertThat(found).isNull();
        verify(repository, times(1)).findById(partId);
        mockedInventoryMapper.verify(() -> InventoryMapper.map(any(InventoryEntity.class)), never()); // Mapper should not be called
    }

    @Test
    @DisplayName("Should propagate exception if repository.findById throws exception")
    void findById_shouldPropagateExceptionFromRepositoryFind() {
        // Given
        RuntimeException repoException = new RuntimeException("DB find error");
        when(repository.findById(partId)).thenThrow(repoException);

        // When / Then
        assertThatThrownBy(() -> inventoryGateway.findById(partId))
                .isInstanceOf(RuntimeException.class)
                .isEqualTo(repoException);

        verify(repository, times(1)).findById(partId);
        mockedInventoryMapper.verifyNoInteractions();
    }

    // --- Test for delete(Long partId) ---
    @Test
    @DisplayName("Should successfully delete an inventory item by ID")
    void delete_shouldDeleteInventorySuccessfully() {
        // Given
        when(repository.findById(partId)).thenReturn(Optional.of(inventoryEntity));
        doNothing().when(repository).delete(inventoryEntity);

        // When
        assertDoesNotThrow(() -> inventoryGateway.delete(partId));

        // Then
        verify(repository, times(1)).findById(partId);
        verify(repository, times(1)).delete(inventoryEntity);
    }

    @Test
    @DisplayName("Should throw RuntimeException when inventory to delete is not found")
    void delete_shouldThrowRuntimeExceptionWhenNotFound() {
        // Given
        when(repository.findById(partId)).thenReturn(Optional.empty());

        // When / Then
        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
            inventoryGateway.delete(partId)
        );

        assertThat(thrown.getMessage()).isEqualTo("Inventory not found");
        verify(repository, times(1)).findById(partId);
        verify(repository, never()).delete(any(InventoryEntity.class)); // Delete should not be called
    }

    @Test
    @DisplayName("Should propagate exception if repository.findById throws exception during delete")
    void delete_shouldPropagateExceptionFromFindById() {
        // Given
        RuntimeException repoException = new RuntimeException("DB find error during delete lookup");
        when(repository.findById(partId)).thenThrow(repoException);

        // When / Then
        assertThatThrownBy(() -> inventoryGateway.delete(partId))
                .isInstanceOf(RuntimeException.class)
                .isEqualTo(repoException);

        verify(repository, times(1)).findById(partId);
        verify(repository, never()).delete(any(InventoryEntity.class));
    }

    @Test
    @DisplayName("Should propagate exception if repository.delete throws exception")
    void delete_shouldPropagateExceptionFromRepositoryDelete() {
        // Given
        when(repository.findById(partId)).thenReturn(Optional.of(inventoryEntity));
        RuntimeException repoException = new RuntimeException("DB delete error");
        doThrow(repoException).when(repository).delete(inventoryEntity);

        // When / Then
        assertThatThrownBy(() -> inventoryGateway.delete(partId))
                .isInstanceOf(RuntimeException.class)
                .isEqualTo(repoException);

        verify(repository, times(1)).findById(partId);
        verify(repository, times(1)).delete(inventoryEntity);
    }
}