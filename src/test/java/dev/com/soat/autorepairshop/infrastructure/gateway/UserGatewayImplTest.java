package dev.com.soat.autorepairshop.infrastructure.gateway;

import dev.com.soat.autorepairshop.domain.enums.SortDirectionType;
import dev.com.soat.autorepairshop.domain.utils.PaginationUtils;
import dev.com.soat.autorepairshop.infrastructure.repository.RoleRepository;
import dev.com.soat.autorepairshop.infrastructure.repository.UserRepository;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.UserEntity;
import dev.com.soat.autorepairshop.mock.RoleMock;
import dev.com.soat.autorepairshop.mock.UserMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserGatewayImplTest {

    @InjectMocks
    private UserGatewayImpl gateway;

    @Mock
    private UserRepository repository;

    @Mock
    private RoleRepository roleRepository;

    @Test
    @DisplayName("Deve salvar um usuário com sucesso.")
    void givenValidUserWhenCreateUserThenReturnSuccessfully() {
        final var domain = UserMock.buildMockDomain();
        final var entity = UserMock.buildMockEntity();
        final var role = RoleMock.buildMockEntity();

        when(roleRepository.findByName(any())).thenReturn(role);
        when(repository.save(any(UserEntity.class))).thenReturn(entity);
        final var user = gateway.save(domain);

        assertNotNull(user);
        assertEquals(domain.getName(), user.getName());
        verify(roleRepository, times(1)).findByName(any());
        verify(repository, times(1)).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("Deve recuperar um usuário com sucesso através do e-mail.")
    void givenValidEmailWhenFindByEmailThenReturnSuccessfully() {
        final var domain = UserMock.buildMockDomain();
        final var entity = UserMock.buildMockEntity();

        when(repository.findByEmail(any(String.class))).thenReturn(Optional.of(entity));
        final var user = gateway.findByEmail(domain.getEmail());

        assertNotNull(user);
        assertEquals(domain.getName(), user.getName());
        verify(repository, times(1)).findByEmail(any(String.class));
    }

    @Test
    @DisplayName("Deve recuperar um usuário com sucesso através do identificador.")
    void givenValidIdentifierWhenFindByEmailThenReturnSuccessfully() {
        final var domain = UserMock.buildMockDomain();
        final var entity = UserMock.buildMockEntity();

        when(repository.findById(domain.getIdentifier())).thenReturn(Optional.of(entity));
        final var user = gateway.findByUserId(domain.getIdentifier());

        assertNotNull(user);
        assertEquals(domain.getName(), user.getName());
        verify(repository, times(1)).findById(domain.getIdentifier());
    }

    @Test
    @DisplayName("Deve recuperar todos os usuários de maneira paginada.")
    void givenSolicitationWhenFindAllByPageThenReturnSuccessfully() {
        final var entity = UserMock.buildMockEntity();
        final var pagination = new PaginationUtils(0, 10, "name", SortDirectionType.ASC);

        final var pageable = PageRequest.of(
                pagination.pageNumber(),
                pagination.pageSize(),
                Sort.by(Sort.Direction.ASC, pagination.sortBy())
        );
        final var page = new PageImpl<>(Collections.singletonList(entity), pageable, 1);

        when(repository.findAll(pageable)).thenReturn(page);
        final var result = gateway.findAllByPage(pagination);

        assertNotNull(result);
        assertFalse(result.content().isEmpty());
        assertEquals(1, result.content().size());
        assertEquals(entity.getName(), result.content().getFirst().getName());
        verify(repository).findAll(pageable);
    }

    @Test
    @DisplayName("Should find a user by document successfully.")
    void givenValidDocumentWhenFindByDocumentThenReturnSuccessfully() {
        final var domain = UserMock.buildMockDomain();
        final var entity = UserMock.buildMockEntity();

        when(repository.findByDocument(any(String.class))).thenReturn(Optional.of(entity));
        final var user = gateway.findByDocument(domain.getDocument());

        assertNotNull(user);
        assertEquals(domain.getName(), user.getName());
        verify(repository, times(1)).findByDocument(any(String.class));
    }

    @Test
    @DisplayName("Should return null when no user is found by document.")
    void givenInvalidDocumentWhenFindByDocumentThenReturnNull() {
        when(repository.findByDocument(any(String.class))).thenReturn(Optional.empty());
        final var user = gateway.findByDocument("52998224725");

        assertNull(user);
        verify(repository, times(1)).findByDocument(any(String.class));
    }

    @Test
    @DisplayName("Should find an employee by ID successfully.")
    void givenValidEmployeeIdWhenFindEmployeeByIdThenReturnSuccessfully() {
        final var domain = UserMock.buildMockDomain();
        final var entity = UserMock.buildMockEntity();

        when(repository.findEmployeeById(domain.getIdentifier())).thenReturn(Optional.of(entity));
        final var employee = gateway.findEmployeeById(domain.getIdentifier());

        assertTrue(employee.isPresent());
        assertEquals(domain.getName(), employee.get().getName());
        verify(repository, times(1)).findEmployeeById(domain.getIdentifier());
    }

    @Test
    @DisplayName("Should return empty when no employee is found by ID.")
    void givenInvalidEmployeeIdWhenFindEmployeeByIdThenReturnEmpty() {
        when(repository.findEmployeeById(any(Long.class))).thenReturn(Optional.empty());
        final var employee = gateway.findEmployeeById(999L);

        assertTrue(employee.isEmpty());
        verify(repository, times(1)).findEmployeeById(any(Long.class));
    }
}