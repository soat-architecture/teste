package dev.com.soat.autorepairshop.application.usecase.part;

import dev.com.soat.autorepairshop.application.models.output.PartOutputDTO;
import dev.com.soat.autorepairshop.domain.entity.PartDomain;
import dev.com.soat.autorepairshop.domain.exception.template.GenericException;
import dev.com.soat.autorepairshop.domain.gateway.PartGateway;
import dev.com.soat.autorepairshop.domain.utils.PageInfoGenericUtils;
import dev.com.soat.autorepairshop.domain.utils.PaginationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FindAllPartPageableUseCaseTest {

    @InjectMocks
    private FindAllPartPageableUseCase useCase;

    @Mock
    private PartGateway gateway;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private PartDomain mockDomain() {
        return new PartDomain(
                1L,
                "Filtro de óleo",
                "SKU-001",
                "Filtro para motor",
                "Bosch",
                new BigDecimal("50.00"),
                new BigDecimal("30.00"),
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void testExecuteWithResults() {
        int page = 0, size = 10;
        String sortBy = "name", direction = "asc";

        PartDomain domain = mockDomain();

        PageInfoGenericUtils<PartDomain> mockPage = new PageInfoGenericUtils<>(
                List.of(domain),
                page,
                size,
                1,
                1
        );

        when(gateway.findAllByPage(any(PaginationUtils.class))).thenReturn(mockPage);

        PageInfoGenericUtils<PartOutputDTO> result = useCase.execute(page, size, sortBy, direction);

        assertNotNull(result);
        assertEquals(1, result.totalElements());
        assertEquals(1, result.content().size());
        assertEquals("SKU-001", result.content().getFirst().sku());

        verify(gateway).findAllByPage(any(PaginationUtils.class));
    }

    @Test
    void testExecuteWithEmptyResult() {
        int page = 0, size = 10;

        PageInfoGenericUtils<PartDomain> emptyPage = new PageInfoGenericUtils<>(
                List.of(),
                page,
                size,
                0,
                0
        );

        when(gateway.findAllByPage(any(PaginationUtils.class))).thenReturn(emptyPage);

        PageInfoGenericUtils<PartOutputDTO> result = useCase.execute(page, size, "name", "asc");

        assertNotNull(result);
        assertTrue(result.content().isEmpty());
        assertEquals(0, result.totalElements());
        assertEquals(0, result.totalPages());

        verify(gateway).findAllByPage(any(PaginationUtils.class));
    }

    @Test
    void testExecuteThrowsGenericException() {
        when(gateway.findAllByPage(any())).thenThrow(new RuntimeException("DB down"));

        assertThrows(GenericException.class, () ->
                useCase.execute(0, 10, "name", "asc"));
    }
}
