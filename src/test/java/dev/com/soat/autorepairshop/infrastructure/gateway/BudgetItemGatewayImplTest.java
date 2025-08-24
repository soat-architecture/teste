package dev.com.soat.autorepairshop.infrastructure.gateway;

import dev.com.soat.autorepairshop.domain.entity.BudgetItemDomain;
import dev.com.soat.autorepairshop.infrastructure.repository.BudgetItemRepository;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.BudgetItemEntity;
import dev.com.soat.autorepairshop.infrastructure.repository.mapper.BudgetItemEntityMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetItemGatewayImplTest {

    @InjectMocks
    private BudgetItemGatewayImpl gateway;

    @Mock
    private BudgetItemRepository repository;

    @Test
    void saveAll_whenItemsPresent_mapsPersistAndMapsBack() {
        // domains de entrada
        BudgetItemDomain d1 = mock(BudgetItemDomain.class);
        BudgetItemDomain d2 = mock(BudgetItemDomain.class);

        // entities mapeadas a partir dos domains
        BudgetItemEntity e1 = mock(BudgetItemEntity.class);
        BudgetItemEntity e2 = mock(BudgetItemEntity.class);

        // entities retornadas pelo repository
        BudgetItemEntity saved1 = mock(BudgetItemEntity.class);
        BudgetItemEntity saved2 = mock(BudgetItemEntity.class);

        // domains finais após mapear de volta
        BudgetItemDomain back1 = mock(BudgetItemDomain.class);
        BudgetItemDomain back2 = mock(BudgetItemDomain.class);

        try (MockedStatic<BudgetItemEntityMapper> mocked = mockStatic(BudgetItemEntityMapper.class)) {
            // toEntity
            mocked.when(() -> BudgetItemEntityMapper.toEntity(eq(d1))).thenReturn(e1);
            mocked.when(() -> BudgetItemEntityMapper.toEntity(eq(d2))).thenReturn(e2);

            // repo.saveAll
            when(repository.saveAll(eq(List.of(e1, e2)))).thenReturn(List.of(saved1, saved2));

            // toDomain (de volta)
            mocked.when(() -> BudgetItemEntityMapper.toDomain(eq(saved1))).thenReturn(back1);
            mocked.when(() -> BudgetItemEntityMapper.toDomain(eq(saved2))).thenReturn(back2);

            // act
            List<BudgetItemDomain> result = gateway.saveAll(List.of(d1, d2));

            // assert
            assertThat(result).containsExactly(back1, back2);

            // verify
            mocked.verify(() -> BudgetItemEntityMapper.toEntity(eq(d1)));
            mocked.verify(() -> BudgetItemEntityMapper.toEntity(eq(d2)));
            verify(repository).saveAll(eq(List.of(e1, e2)));
            mocked.verify(() -> BudgetItemEntityMapper.toDomain(eq(saved1)));
            mocked.verify(() -> BudgetItemEntityMapper.toDomain(eq(saved2)));
            verifyNoMoreInteractions(repository);
        }
    }

    @Test
    void saveAll_whenEmpty_returnsEmptyAndDoesNotHitRepository() {
        List<BudgetItemDomain> result = gateway.saveAll(List.of());
        assertThat(result).isEmpty();
        verifyNoInteractions(repository);
    }

    @Test
    void findByBudgetId_whenIdIsNull_returnsEmptyAndDoesNotHitRepository() {
        List<BudgetItemDomain> result = gateway.findByBudgetId(null);

        assertThat(result).isEmpty();
        verifyNoInteractions(repository);
    }

    @Test
    void findByBudgetId_whenIdPresent_fetchesFromRepository_andMapsToDomain() {
        Long budgetId = 123L;

        // entities vindas do repository
        BudgetItemEntity e1 = mock(BudgetItemEntity.class);
        BudgetItemEntity e2 = mock(BudgetItemEntity.class);
        when(repository.findAllByBudgetId(budgetId)).thenReturn(List.of(e1, e2));

        // domains após mapear
        BudgetItemDomain d1 = mock(BudgetItemDomain.class);
        BudgetItemDomain d2 = mock(BudgetItemDomain.class);

        try (MockedStatic<BudgetItemEntityMapper> mocked = mockStatic(BudgetItemEntityMapper.class)) {
            mocked.when(() -> BudgetItemEntityMapper.toDomain(eq(e1))).thenReturn(d1);
            mocked.when(() -> BudgetItemEntityMapper.toDomain(eq(e2))).thenReturn(d2);

            List<BudgetItemDomain> result = gateway.findByBudgetId(budgetId);

            assertThat(result).containsExactly(d1, d2);

            verify(repository).findAllByBudgetId(budgetId);
            mocked.verify(() -> BudgetItemEntityMapper.toDomain(eq(e1)));
            mocked.verify(() -> BudgetItemEntityMapper.toDomain(eq(e2)));
            verifyNoMoreInteractions(repository);
        }
    }
}

