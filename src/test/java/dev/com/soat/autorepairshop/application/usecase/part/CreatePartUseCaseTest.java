
package dev.com.soat.autorepairshop.application.usecase.part;

import dev.com.soat.autorepairshop.application.mapper.InventoryApplicationMapper;
import dev.com.soat.autorepairshop.application.mapper.PartApplicationMapper;
import dev.com.soat.autorepairshop.application.models.input.PartInputDTO;
import dev.com.soat.autorepairshop.domain.entity.InventoryDomain;
import dev.com.soat.autorepairshop.domain.entity.PartDomain;
import dev.com.soat.autorepairshop.domain.exception.template.ConflictException;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.exception.template.GenericException;
import dev.com.soat.autorepairshop.domain.gateway.InventoryGateway;
import dev.com.soat.autorepairshop.domain.gateway.PartGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para CreatePartUseCase")
class CreatePartUseCaseTest {

    @Mock
    private PartGateway partGateway;

    @Mock
    private InventoryGateway inventoryGateway;

    @InjectMocks
    private CreatePartUseCase createPartUseCase;

    private PartInputDTO validPartInputDTO;
    private PartDomain partDomain;
    private InventoryDomain inventoryDomain;

    @BeforeEach
    void setUp() {
        validPartInputDTO = new PartInputDTO(
                "Pastilha de freio",
                "SKU-001",
                "Pastilha dianteira",
                "Bosch",
                new BigDecimal("100.00"),
                new BigDecimal("70.00")
        );

        partDomain = new PartDomain(
                1L,
                "Pastilha de freio",
                "SKU-001",
                "Pastilha dianteira",
                "Bosch",
                new BigDecimal("100.00"),
                new BigDecimal("70.00"),
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        inventoryDomain = new InventoryDomain(
                1L,
                1,
                LocalDateTime.now(),
                null
        );
    }

    @Test
    @DisplayName("Deve criar peça com sucesso quando dados são válidos")
    void shouldCreatePartSuccessfully() {
        // given
        when(partGateway.findBySku("SKU-001")).thenReturn(null);
        when(partGateway.save(any(PartDomain.class))).thenReturn(partDomain);

        try (MockedStatic<PartApplicationMapper> partMapperMock = mockStatic(PartApplicationMapper.class);
             MockedStatic<InventoryApplicationMapper> inventoryMapperMock = mockStatic(InventoryApplicationMapper.class)) {

            partMapperMock.when(() -> PartApplicationMapper.toDomain(validPartInputDTO))
                    .thenReturn(partDomain);

            inventoryMapperMock.when(() -> InventoryApplicationMapper.mapToDomain(partDomain.getIdentifier()))
                    .thenReturn(inventoryDomain);

            // when
            createPartUseCase.execute(validPartInputDTO);

            // then
            verify(partGateway).save(partDomain);
            verify(inventoryGateway).saveNotReturns(inventoryDomain);
        }
    }

    @Test
    @DisplayName("Deve lançar ConflictException quando SKU já existe")
    void shouldThrowConflictExceptionWhenSkuExists() {
        // given
        when(partGateway.findBySku("SKU-001")).thenReturn(partDomain);

        // when/then
        assertThrows(ConflictException.class, () -> createPartUseCase.execute(validPartInputDTO));

        verify(partGateway, never()).save(any());
        verify(inventoryGateway, never()).saveNotReturns(any());
    }

    @Test
    @DisplayName("Deve lançar DomainException quando ocorrer erro de domínio")
    void shouldThrowDomainExceptionWhenDomainError() {
        // given
        when(partGateway.findBySku("SKU-001")).thenReturn(null);

        try (MockedStatic<PartApplicationMapper> partMapperMock = mockStatic(PartApplicationMapper.class)) {
            partMapperMock.when(() -> PartApplicationMapper.toDomain(validPartInputDTO))
                    .thenThrow(new DomainException("domain.error"));

            // when/then
            assertThrows(DomainException.class, () -> createPartUseCase.execute(validPartInputDTO));

            verify(partGateway, never()).save(any());
            verify(inventoryGateway, never()).saveNotReturns(any());
        }
    }

    @Test
    @DisplayName("Deve lançar GenericException quando ocorrer erro inesperado")
    void shouldThrowGenericExceptionWhenUnexpectedError() {
        // given
        when(partGateway.findBySku("SKU-001")).thenThrow(new RuntimeException("Unexpected error"));

        // when/then
        assertThrows(GenericException.class, () -> createPartUseCase.execute(validPartInputDTO));

        verify(partGateway, never()).save(any());
        verify(inventoryGateway, never()).saveNotReturns(any());
    }
}