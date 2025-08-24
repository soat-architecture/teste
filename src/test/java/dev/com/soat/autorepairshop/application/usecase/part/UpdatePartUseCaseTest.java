package dev.com.soat.autorepairshop.application.usecase.part;

import dev.com.soat.autorepairshop.application.models.input.PartInputDTO;
import dev.com.soat.autorepairshop.domain.entity.PartDomain;
import dev.com.soat.autorepairshop.domain.exception.template.ConflictException;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.exception.template.ValidatorException;
import dev.com.soat.autorepairshop.domain.gateway.PartGateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

class UpdatePartUseCaseTest {

    @InjectMocks
    private UpdatePartUseCase updatePartUseCase;

    @Mock
    private PartGateway partGateway;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private PartDomain mockPartDomain(Long id, String sku, boolean active) {
        return new PartDomain(
                id,
                "Pastilha de freio",
                sku,
                "Pastilha dianteira",
                "Bosch",
                new BigDecimal("100.00"),
                new BigDecimal("70.00"),
                active,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void testExecuteWithValidUpdate() {
        Long id = 1L;
        PartInputDTO dto = new PartInputDTO(
                "Pastilha de freio", "SKU-001", "Atualizada", "Bosch",
                new BigDecimal("120.00"), new BigDecimal("80.00")
        );

        PartDomain existing = mockPartDomain(id, "SKU-001", true);
        Mockito.when(partGateway.findById(id)).thenReturn(existing);

        Assertions.assertDoesNotThrow(() -> updatePartUseCase.execute(id, dto));

        Mockito.verify(partGateway).update(Mockito.eq(existing), Mockito.any());
    }

    @Test
    void testExecuteWithNotFoundIdThrows404() {
        Long id = 1L;
        PartInputDTO dto = new PartInputDTO("Item", "SKU-001", "desc", "Marca",
                BigDecimal.ONE, BigDecimal.ONE);

        Mockito.when(partGateway.findById(id)).thenReturn(null);

        NotFoundException ex = Assertions.assertThrows(NotFoundException.class,
                () -> updatePartUseCase.execute(id, dto));
    }

    @Test
    void testExecuteWithInactivePartThrows400() {
        Long id = 1L;
        PartInputDTO dto = new PartInputDTO("Item", "SKU-001", "desc", "Marca",
                BigDecimal.ONE, BigDecimal.ONE);

        PartDomain inactive = mockPartDomain(id, "SKU-001", false);
        Mockito.when(partGateway.findById(id)).thenReturn(inactive);

        ValidatorException ex = Assertions.assertThrows(ValidatorException.class,
                () -> updatePartUseCase.execute(id, dto));
    }

    @Test
    void testExecuteWithDuplicatedSkuThrows409() {
        Long id = 1L;
        PartInputDTO dto = new PartInputDTO("Item", "SKU-002", "desc", "Marca",
                BigDecimal.ONE, BigDecimal.ONE);

        PartDomain existing = mockPartDomain(id, "SKU-001", true);
        PartDomain conflicting = mockPartDomain(2L, "SKU-002", true);

        Mockito.when(partGateway.findById(id)).thenReturn(existing);
        Mockito.when(partGateway.findBySku("SKU-002")).thenReturn(conflicting);

        ConflictException ex = Assertions.assertThrows(ConflictException.class,
                () -> updatePartUseCase.execute(id, dto));
    }
}
