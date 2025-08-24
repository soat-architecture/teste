package dev.com.soat.autorepairshop.application.usecase.user;

import dev.com.soat.autorepairshop.domain.entity.UserDomain;
import dev.com.soat.autorepairshop.domain.enums.SortDirectionType;
import dev.com.soat.autorepairshop.domain.exception.template.GenericException;
import dev.com.soat.autorepairshop.domain.gateway.UserGateway;
import dev.com.soat.autorepairshop.domain.utils.PageInfoGenericUtils;
import dev.com.soat.autorepairshop.domain.utils.PaginationUtils;
import dev.com.soat.autorepairshop.mock.UserMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindAllUsersPageableUseCaseTest {

    @Mock
    private UserGateway userGateway;

    @InjectMocks
    private FindAllUsersPageableUseCase findAllUsersPageableUseCase;

    @Test
    @DisplayName("Deve retornar lista paginada de usuários quando existirem registros")
    void shouldReturnPagedUserListWhenUsersExist() {
        // Given
        final int page = 0;
        final int size = 10;
        final String sortBy = "name";
        final String direction = "ASC";

        final var userA = UserMock.buildMockDomain();
        final var userB = UserMock.buildMockDomain();
        final var users = List.of(userA, userB);

        final var pageInfo = new PageInfoGenericUtils<>(users, page, size, 2L, 1);

        when(userGateway.findAllByPage(any(PaginationUtils.class))).thenReturn(pageInfo);

        // When
        final var result = findAllUsersPageableUseCase.execute(page, size, sortBy, direction);

        // Then
        assertNotNull(result);
        assertEquals(2, result.content().size());
        assertEquals(2L, result.totalElements());
        assertEquals(1, result.totalPages());
        assertEquals(page, result.pageNumber());
        assertEquals(size, result.pageSize());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não existirem usuários")
    void shouldReturnEmptyListWhenNoUsersExist() {
        // Given
        final int page = 0;
        final int size = 10;
        final String sortBy = "name";
        final String direction = "ASC";

        final var emptyPage = new PageInfoGenericUtils<UserDomain>(
                Collections.emptyList(),
                page,
                size,
                0L,
                0
        );

        when(userGateway.findAllByPage(any(PaginationUtils.class))).thenReturn(emptyPage);

        // When
        final var result = findAllUsersPageableUseCase.execute(page, size, sortBy, direction);

        // Then
        assertNotNull(result);
        assertTrue(result.content().isEmpty());
        assertEquals(0L, result.totalElements());
        assertEquals(0, result.totalPages());
    }

    @Test
    @DisplayName("Deve lançar GenericException quando ocorrer erro na busca")
    void shouldThrowGenericExceptionWhenErrorOccurs() {
        // Given
        final int page = 0;
        final int size = 10;
        final String sortBy = "name";
        final String direction = "ASC";

        when(userGateway.findAllByPage(any(PaginationUtils.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When/Then
        assertThrows(GenericException.class, () ->
                findAllUsersPageableUseCase.execute(page, size, sortBy, direction));
    }

    @Test
    @DisplayName("Deve criar paginação corretamente com diferentes direções de ordenação")
    void shouldCreatePaginationWithDifferentSortDirections() {
        // Given
        final int page = 0;
        final int size = 10;
        final String sortBy = "name";
        final String direction = "DESC";

        final var emptyPage = new PageInfoGenericUtils<UserDomain>(
                Collections.emptyList(),
                page,
                size,
                0L,
                0
        );

        when(userGateway.findAllByPage(any(PaginationUtils.class))).thenReturn(emptyPage);

        // When
        final var result = findAllUsersPageableUseCase.execute(page, size, sortBy, direction);

        // Then
        assertNotNull(result);
        verify(userGateway).findAllByPage(argThat(pagination ->
                pagination.direction().equals(SortDirectionType.DESC)
        ));
    }
}