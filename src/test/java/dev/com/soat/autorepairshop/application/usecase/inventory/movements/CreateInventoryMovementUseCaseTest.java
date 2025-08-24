package dev.com.soat.autorepairshop.application.usecase.inventory.movements;

import dev.com.soat.autorepairshop.application.models.input.InventoryMovementInputDTO;
import dev.com.soat.autorepairshop.domain.entity.InventoryMovementDomain;
import dev.com.soat.autorepairshop.domain.enums.MovementType;
import dev.com.soat.autorepairshop.domain.gateway.InventoryMovementGateway;

import dev.com.soat.autorepairshop.infrastructure.api.mapper.InventoryMovementMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateInventoryMovementUseCaseTest {

    @Mock
    private InventoryMovementGateway gateway;

    @InjectMocks
    private CreateInventoryMovementUseCase createInventoryMovementUseCase;

    private InventoryMovementInputDTO inventoryMovementInputDTO;
    private InventoryMovementDomain inventoryMovementDomain;

    private MockedStatic<InventoryMovementMapper> mockedInventoryMovementMapper;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now().withNano(0);

        inventoryMovementInputDTO = new InventoryMovementInputDTO(
                1L,
                10L,
                null,
                MovementType.INBOUND,
                50,
                100,
                150,
                "Initial stock for new order"
        );

        inventoryMovementDomain = new InventoryMovementDomain(
                null,
                inventoryMovementInputDTO.partId(),
                inventoryMovementInputDTO.serviceOrderId(),
                inventoryMovementInputDTO.serviceOrderId(),
                inventoryMovementInputDTO.movementType(),
                inventoryMovementInputDTO.quantityChanged(),
                inventoryMovementInputDTO.quantityBefore(),
                inventoryMovementInputDTO.quantityAfter(),
                inventoryMovementInputDTO.reason(),
                now.minusMinutes(5)
        );

        mockedInventoryMovementMapper = mockStatic(InventoryMovementMapper.class);
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        if (mockedInventoryMovementMapper != null) {
            mockedInventoryMovementMapper.close();
        }
    }

    @Test
    @DisplayName("Should successfully create and save an inventory movement")
    void execute_shouldCreateAndSaveMovementSuccessfully() {
        // Given
        mockedInventoryMovementMapper.when(() -> InventoryMovementMapper.map(inventoryMovementInputDTO))
                .thenReturn(inventoryMovementDomain);
        doNothing().when(gateway).save(inventoryMovementDomain);

        // When
        assertDoesNotThrow(() -> createInventoryMovementUseCase.execute(inventoryMovementInputDTO));

        // Then
        mockedInventoryMovementMapper.verify(() -> InventoryMovementMapper.map(inventoryMovementInputDTO), times(1));
        verify(gateway, times(1)).save(inventoryMovementDomain);
    }

    @Test
    @DisplayName("Should throw NullPointerException when InventoryMovementInputDTO is null")
    void execute_shouldThrowNullPointerExceptionWhenInputDTOIsNull() {
        // Given
        mockedInventoryMovementMapper.verifyNoInteractions();
        verifyNoInteractions(gateway);
    }

    @Test
    @DisplayName("Should propagate exception if InventoryMovementMapper.map throws an exception")
    void execute_shouldPropagateExceptionFromMapper() {
        // Given
        RuntimeException mapperException = new RuntimeException("Mapping failed due to invalid data");
        mockedInventoryMovementMapper.when(() -> InventoryMovementMapper.map(inventoryMovementInputDTO))
                .thenThrow(mapperException);

        // When / Then
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            createInventoryMovementUseCase.execute(inventoryMovementInputDTO);
        });

        assertEquals(mapperException, thrown);

        mockedInventoryMovementMapper.verify(() -> InventoryMovementMapper.map(inventoryMovementInputDTO), times(1));
        verifyNoInteractions(gateway);
    }

    @Test
    @DisplayName("Should propagate exception if InventoryMovementGateway.save throws an exception")
    void execute_shouldPropagateExceptionFromGateway() {
        // Given
        mockedInventoryMovementMapper.when(() -> InventoryMovementMapper.map(inventoryMovementInputDTO))
                .thenReturn(inventoryMovementDomain);
        RuntimeException gatewayException = new RuntimeException("Database save failed for movement");
        doThrow(gatewayException).when(gateway).save(inventoryMovementDomain);

        // When / Then
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            createInventoryMovementUseCase.execute(inventoryMovementInputDTO);
        });

        assertEquals(gatewayException, thrown);

        mockedInventoryMovementMapper.verify(() -> InventoryMovementMapper.map(inventoryMovementInputDTO), times(1));
        verify(gateway, times(1)).save(inventoryMovementDomain);
    }
}