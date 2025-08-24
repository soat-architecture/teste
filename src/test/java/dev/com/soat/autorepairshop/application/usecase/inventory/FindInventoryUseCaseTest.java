package dev.com.soat.autorepairshop.application.usecase.inventory;

import dev.com.soat.autorepairshop.application.mapper.InventoryApplicationMapper;
import dev.com.soat.autorepairshop.application.models.output.InventoryOutputDTO;
import dev.com.soat.autorepairshop.domain.gateway.InventoryGateway;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.InventoryRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindInventoryUseCaseTest {

    @Mock
    private InventoryGateway inventoryGateway;

    @InjectMocks
    private FindInventoryUseCase findInventoryUseCase;

    private Long partId;
    private MockedStatic<InventoryApplicationMapper> mockedInventoryApplicationMapper;

    @BeforeEach
    void setUp() {
        partId = 123L;
        mockedInventoryApplicationMapper = mockStatic(InventoryApplicationMapper.class);
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        if (mockedInventoryApplicationMapper != null) {
            mockedInventoryApplicationMapper.close();
        }
    }

    @Test
    @DisplayName("Should return null when inventory is not found")
    void execute_shouldReturnNullWhenInventoryIsNotFound() {
        // Given
        when(inventoryGateway.findById(partId)).thenReturn(null);
        mockedInventoryApplicationMapper.when(() -> InventoryApplicationMapper.map((InventoryRequestDTO) null)).thenReturn(null);

        // When
        InventoryOutputDTO result = findInventoryUseCase.execute(partId);

        // Then
        assertNull(result);

        verify(inventoryGateway, times(1)).findById(partId);
    }

    @Test
    @DisplayName("Should propagate exception if InventoryGateway.findById throws an exception")
    void execute_shouldPropagateExceptionFromGateway() {
        // Given
        RuntimeException gatewayException = new RuntimeException("Database connection error");
        when(inventoryGateway.findById(partId)).thenThrow(gatewayException);

        // When / Then
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            findInventoryUseCase.execute(partId);
        });

        assertEquals(gatewayException, thrown);

        verify(inventoryGateway, times(1)).findById(partId);
    }
}