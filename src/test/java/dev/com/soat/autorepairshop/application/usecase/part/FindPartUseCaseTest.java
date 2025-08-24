package dev.com.soat.autorepairshop.application.usecase.part;

import dev.com.soat.autorepairshop.application.models.output.PartOutputDTO;
import dev.com.soat.autorepairshop.domain.entity.PartDomain;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.gateway.PartGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FindPartUseCaseTest {

    @InjectMocks
    private FindPartUseCase findPartUseCase;

    @Mock
    private PartGateway partGateway;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private PartDomain mockPart(Long id) {
        return new PartDomain(
                id,
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
    }

    @Test
    void testFindByIdWithSuccess() {
        Long id = 1L;
        PartDomain part = mockPart(id);

        when(partGateway.findById(id)).thenReturn(part);

        PartOutputDTO result = assertDoesNotThrow(() -> findPartUseCase.execute(id));

        assertEquals(part.getName(), result.name());
        assertEquals(part.getSku(), result.sku());
        assertEquals(part.getDescription(), result.description());
        assertEquals(part.getBrand(), result.brand());
        assertEquals(part.getSellingPrice(), result.sellingPrice());
        assertEquals(part.getBuyPrice(), result.buyPrice());

        verify(partGateway).findById(id);
    }

    @Test
    void testFindByIdNotFoundShouldThrow404() {
        Long id = 999L;

        when(partGateway.findById(id)).thenReturn(null);

        DomainException ex = assertThrows(DomainException.class,
                () -> findPartUseCase.execute(id));

        verify(partGateway).findById(id);
    }
}
