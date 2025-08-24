package dev.com.soat.autorepairshop.application.usecase.part;

import dev.com.soat.autorepairshop.domain.entity.PartDomain;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.exception.template.ValidatorException;
import dev.com.soat.autorepairshop.domain.gateway.PartGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class DeletePartUseCaseTest {

    @InjectMocks
    private DeletePartUseCase deletePartUseCase;

    @Mock
    private PartGateway partGateway;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private PartDomain mockPart(Long id, boolean active) {
        return new PartDomain(
                id,
                "Pastilha de freio",
                "SKU-001",
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
    void testDeleteWithValidId() {
        Long id = 1L;
        PartDomain part = mockPart(id, true);

        when(partGateway.findById(id)).thenReturn(part);

        assertDoesNotThrow(() -> deletePartUseCase.execute(id));

        verify(partGateway).findById(id);
        verify(partGateway).delete(id);
    }

    @Test
    void testDeleteWithNotFoundIdShouldThrow404() {
        Long id = 1L;

        when(partGateway.findById(id)).thenReturn(null);

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> deletePartUseCase.execute(id));

        verify(partGateway).findById(id);
        verify(partGateway, never()).delete(any());
    }

    @Test
    void testDeleteWithInactivePartShouldThrow409() {
        Long id = 1L;
        PartDomain inactive = mockPart(id, false);

        when(partGateway.findById(id)).thenReturn(inactive);

        ValidatorException ex = assertThrows(ValidatorException.class,
                () -> deletePartUseCase.execute(id));

        verify(partGateway).findById(id);
        verify(partGateway, never()).delete(any());
    }
}
