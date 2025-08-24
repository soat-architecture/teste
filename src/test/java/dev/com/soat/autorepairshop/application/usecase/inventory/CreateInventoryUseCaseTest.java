package dev.com.soat.autorepairshop.application.usecase.inventory;

import dev.com.soat.autorepairshop.application.models.input.InventoryInputDTO;
import dev.com.soat.autorepairshop.application.models.input.InventoryMovementInputDTO;
import dev.com.soat.autorepairshop.application.usecase.inventory.movements.CreateInventoryMovementUseCase;
import dev.com.soat.autorepairshop.domain.entity.InventoryDomain;
import dev.com.soat.autorepairshop.domain.entity.PartDomain;
import dev.com.soat.autorepairshop.domain.enums.MovementType;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.gateway.InventoryGateway;
import dev.com.soat.autorepairshop.domain.gateway.PartGateway;
import dev.com.soat.autorepairshop.mock.PartMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateInventoryUseCaseTest {

    @InjectMocks
    private CreateInventoryUseCase createInventoryUseCase;

    @Mock
    private InventoryGateway inventoryGateway;

    @Mock
    private PartGateway partGateway;

    @Mock
    private CreateInventoryMovementUseCase createInventoryMovementUseCase;

    @Captor
    private ArgumentCaptor<InventoryMovementInputDTO> movementCaptor;

    private InventoryInputDTO validInventoryInput;
    private PartDomain validPart;

    @BeforeEach
    void setUp() {
        validPart = PartMock.buildDomain();

        validInventoryInput = new InventoryInputDTO(
                1L,
                10,
                MovementType.INITIAL
        );
    }

    @Test
    @DisplayName("Deve criar inventário com sucesso quando dados são válidos")
    void shouldCreateInventorySuccessfully() {
        // Arrange
        when(partGateway.findById(validInventoryInput.partId())).thenReturn(validPart);

        // Act
        createInventoryUseCase.execute(validInventoryInput);

        // Assert
        verify(createInventoryMovementUseCase).execute(movementCaptor.capture());
        verify(inventoryGateway).save(any(InventoryDomain.class));

        InventoryMovementInputDTO capturedMovement = movementCaptor.getValue();
        assertEquals(validPart.getIdentifier(), capturedMovement.partId());
        assertEquals(MovementType.INITIAL, capturedMovement.movementType());
        assertEquals(validInventoryInput.quantityChanged(), capturedMovement.quantityChanged());
        assertEquals("Initial inventory", capturedMovement.reason());
    }

    @Test
    @DisplayName("Deve lançar exceção quando peça não for encontrada")
    void shouldThrowExceptionWhenPartNotFound() {
        // Arrange
        when(partGateway.findById(validInventoryInput.partId())).thenReturn(null);

        // Act & Assert
        DomainException exception = assertThrows(DomainException.class, () ->
                createInventoryUseCase.execute(validInventoryInput)
        );

        assertEquals("part.not.found", exception.getMessage());
        verify(createInventoryMovementUseCase, never()).execute(any());
        verify(inventoryGateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve garantir que a transação seja executada na ordem correta")
    void shouldEnsureCorrectTransactionOrder() {
        // Arrange
        when(partGateway.findById(validInventoryInput.partId())).thenReturn(validPart);

        // Act
        createInventoryUseCase.execute(validInventoryInput);

        // Assert
        var inOrder = inOrder(createInventoryMovementUseCase, inventoryGateway);
        inOrder.verify(createInventoryMovementUseCase).execute(any());
        inOrder.verify(inventoryGateway).save(any());
    }
}