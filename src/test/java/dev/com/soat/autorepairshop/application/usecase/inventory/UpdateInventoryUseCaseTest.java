package dev.com.soat.autorepairshop.application.usecase.inventory;

import dev.com.soat.autorepairshop.application.models.input.InventoryInputDTO;
import dev.com.soat.autorepairshop.application.models.input.InventoryMovementInputDTO;
import dev.com.soat.autorepairshop.application.usecase.inventory.movements.CreateInventoryMovementUseCase;
import dev.com.soat.autorepairshop.domain.entity.InventoryDomain;
import dev.com.soat.autorepairshop.domain.enums.MovementType;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.gateway.InventoryGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UpdateInventoryUseCaseTest {

    @Mock
    private InventoryGateway gateway;

    @Mock
    private CreateInventoryMovementUseCase createMovementUseCase;

    @InjectMocks
    private UpdateInventoryUseCase useCase;

    private InventoryDomain existingInventory;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        existingInventory = new InventoryDomain(
                1L,
                100,
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(1)
        );
    }

    @Test
    void shouldUpdateInventoryWithInbound() {
        InventoryInputDTO input = new InventoryInputDTO(1L, 50, MovementType.INBOUND);

        when(gateway.findById(1L)).thenReturn(existingInventory);

        useCase.execute(1L, input);

        ArgumentCaptor<InventoryMovementInputDTO> captor = ArgumentCaptor.forClass(InventoryMovementInputDTO.class);
        verify(createMovementUseCase).execute(captor.capture());

        InventoryMovementInputDTO movementDTO = captor.getValue();
        assertEquals(MovementType.INBOUND, movementDTO.movementType());
        assertEquals(50, movementDTO.quantityChanged());
        assertEquals(100, movementDTO.quantityBefore());
        assertEquals(150, movementDTO.quantityAfter());

        verify(gateway).update(eq(existingInventory), any(InventoryDomain.class));
    }

    @Test
    void shouldThrowWhenInventoryNotFound() {
        when(gateway.findById(1L)).thenReturn(null);

        InventoryInputDTO input = new InventoryInputDTO(1L, 10, MovementType.ADJUSTMENT);

        DomainException ex = assertThrows(DomainException.class, () -> useCase.execute(1L, input));
        assertEquals("Inventory not found", ex.getMessage());

        verify(createMovementUseCase, never()).execute(any());
        verify(gateway, never()).update(any(), any());
    }
}
