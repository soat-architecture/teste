package dev.com.soat.autorepairshop.application.usecase.order;

import dev.com.soat.autorepairshop.application.mapper.ServiceApplicationMapper;
import dev.com.soat.autorepairshop.application.models.output.ServiceOutputDTO;
import dev.com.soat.autorepairshop.application.usecase.service.FindAllServicesPageableUseCase;
import dev.com.soat.autorepairshop.domain.entity.ServiceDomain;
import dev.com.soat.autorepairshop.domain.exception.template.GenericException;
import dev.com.soat.autorepairshop.domain.gateway.ServiceGateway;
import dev.com.soat.autorepairshop.domain.utils.PageInfoGenericUtils;
import dev.com.soat.autorepairshop.domain.utils.PaginationUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para FindAllServicesPageableUseCase")
class FindAllServicesPageableUseCaseTest {

    @Mock
    private ServiceGateway serviceGateway;

    @InjectMocks
    private FindAllServicesPageableUseCase findAllServicesPageableUseCase;

    private static final int PAGE = 0;
    private static final int SIZE = 10;
    private static final String SORT_BY = "name";
    private static final String DIRECTION = "ASC";

    @Test
    @DisplayName("Deve retornar lista paginada de serviços com sucesso")
    void shouldReturnPagedServicesSuccessfully() {
        // given
        List<ServiceDomain> serviceDomains = createServiceDomains();
        List<ServiceOutputDTO> serviceOutputDTOs = createServiceOutputDTOs();
        PageInfoGenericUtils<ServiceDomain> pageInfo = createPageInfo(serviceDomains);

        when(serviceGateway.findAllByPage(any(PaginationUtils.class))).thenReturn(pageInfo);

        try (MockedStatic<ServiceApplicationMapper> mockedStatic = mockStatic(ServiceApplicationMapper.class)) {
            mockedStatic.when(() -> ServiceApplicationMapper.toDTO(any(ServiceDomain.class)))
                    .thenReturn(serviceOutputDTOs.getFirst());

            // when
            PageInfoGenericUtils<ServiceOutputDTO> result = findAllServicesPageableUseCase
                    .execute(PAGE, SIZE, SORT_BY, DIRECTION);

            // then
            assertNotNull(result);
            assertEquals(PAGE, result.pageNumber());
            assertEquals(SIZE, result.pageSize());
            assertEquals(1, result.totalPages());
            assertEquals(1, result.totalElements());
            assertEquals(1, result.content().size());
        }
    }

    @Test
    @DisplayName("Deve retornar página vazia quando não houver serviços")
    void shouldReturnEmptyPageWhenNoServices() {
        // given
        PageInfoGenericUtils<ServiceDomain> emptyPage = createEmptyPageInfo();
        when(serviceGateway.findAllByPage(any(PaginationUtils.class))).thenReturn(emptyPage);

        // when
        PageInfoGenericUtils<ServiceOutputDTO> result = findAllServicesPageableUseCase
                .execute(PAGE, SIZE, SORT_BY, DIRECTION);

        // then
        assertNotNull(result);
        assertTrue(result.content().isEmpty());
        assertEquals(PAGE, result.pageNumber());
        assertEquals(SIZE, result.pageSize());
        assertEquals(0, result.totalPages());
        assertEquals(0, result.totalElements());
    }

    @Test
    @DisplayName("Deve lançar GenericException quando ocorrer erro")
    void shouldThrowGenericExceptionWhenError() {
        // given
        when(serviceGateway.findAllByPage(any(PaginationUtils.class)))
                .thenThrow(new RuntimeException("Erro inesperado"));

        // when/then
        assertThrows(GenericException.class, () ->
                findAllServicesPageableUseCase.execute(PAGE, SIZE, SORT_BY, DIRECTION));
    }

    private List<ServiceDomain> createServiceDomains() {
        return List.of(new ServiceDomain(
                1L,
                "Troca de óleo",
                "Troca de óleo do motor",
                BigDecimal.valueOf(100.0),
                LocalDateTime.now(),
                null
        ));
    }

    private List<ServiceOutputDTO> createServiceOutputDTOs() {
        return List.of(new ServiceOutputDTO(
                1L,
                "Troca de óleo",
                "Troca de óleo do motor",
                BigDecimal.valueOf(100.0),
                LocalDateTime.now(),
                null
        ));
    }

    private PageInfoGenericUtils<ServiceDomain> createPageInfo(List<ServiceDomain> content) {
        return new PageInfoGenericUtils<>(
                content,
                PAGE,
                SIZE,
                content.size(),
                1
        );
    }

    private PageInfoGenericUtils<ServiceDomain> createEmptyPageInfo() {
        return new PageInfoGenericUtils<>(
                Collections.emptyList(),
                PAGE,
                SIZE,
                0,
                0
        );
    }
}