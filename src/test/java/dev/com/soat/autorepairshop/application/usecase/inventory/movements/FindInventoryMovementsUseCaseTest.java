package dev.com.soat.autorepairshop.application.usecase.inventory.movements;

import dev.com.soat.autorepairshop.application.models.output.InventoryMovementOutputDTO;
import dev.com.soat.autorepairshop.domain.entity.InventoryMovementDomain;
import dev.com.soat.autorepairshop.domain.entity.PartDomain;
import dev.com.soat.autorepairshop.domain.enums.MovementType;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.gateway.InventoryMovementGateway;
import dev.com.soat.autorepairshop.domain.gateway.PartGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindInventoryMovementsUseCaseTest {

    @Mock
    private PartGateway partGateway;

    @Mock
    private InventoryMovementGateway inventoryMovementGateway;

    @InjectMocks
    private FindInventoryMovementsUseCase findInventoryMovementsUseCase;

    private PartDomain part;

    private InventoryMovementDomain inventoryMovementDomain;

    @BeforeEach
    void setUp(){
        part = PartDomain.restore(
                1L,
                "TESTE",
                "123A",
                "TESTE",
                "TESTE",
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(100),
                Boolean.TRUE,
                LocalDateTime.now(),
                LocalDateTime.now()
                );

        inventoryMovementDomain = new InventoryMovementDomain(
                1L,
                1L,
                1L,
                1L,
                MovementType.INBOUND,
                50,
                100,
                150,
                "Received new products",
                LocalDateTime.now().minusMinutes(5)
        );
    }

    @Test
    void shouldReturnInventoryMovements(){
        List<InventoryMovementDomain> result = List.of(inventoryMovementDomain);

        when(partGateway.findBySku("123A")).thenReturn(part);
        when(inventoryMovementGateway.findByPartId(part.getIdentifier())).thenReturn(result);

        List<InventoryMovementOutputDTO> response = findInventoryMovementsUseCase.execute("123A");

        assertNotNull(response);
    }

    @Test
    void whenPartGatewayFindBySkuReturnsNull_ShouldThrowNotFound(){
        when(partGateway.findBySku("123A")).thenReturn(null);
        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> findInventoryMovementsUseCase.execute("123A")
        );

        assertEquals("Part not found.", thrown.getMessage());
    }
}
