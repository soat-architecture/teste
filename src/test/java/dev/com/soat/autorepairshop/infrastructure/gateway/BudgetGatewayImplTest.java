package dev.com.soat.autorepairshop.infrastructure.gateway;

import dev.com.soat.autorepairshop.domain.entity.BudgetDomain;
import dev.com.soat.autorepairshop.infrastructure.repository.BudgetRepository;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.BudgetEntity;
import dev.com.soat.autorepairshop.infrastructure.repository.mapper.BudgetEntityMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import dev.com.soat.autorepairshop.domain.enums.BudgetStatus;
import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BudgetGatewayImplTest {

    @InjectMocks
    private BudgetGatewayImpl gateway;

    @Mock
    private BudgetRepository repository;

    private BudgetEntity entity;

    @BeforeEach
    void setUp() {
        entity = new BudgetEntity();
        entity.setBudgetId(3L);
        entity.setServiceOrderId(10L);
        entity.setVersion(1);
        entity.setTotalAmount(new BigDecimal("500.00"));
        entity.setStatus(BudgetStatus.PENDING_APPROVAL);
        entity.setNotes("Urgent request");
        entity.setCreatedAt(LocalDateTime.now().minusDays(2));
        entity.setEvaluatedAt(LocalDateTime.now().minusDays(1));
    }

    @Test
    void testFindById_whenFound_thenReturnsDomain() {
        when(repository.findById(3L)).thenReturn(Optional.of(entity));

        // Isola o mapper para não impactar outros testes
        try (MockedStatic<BudgetEntityMapper> mocked = mockStatic(BudgetEntityMapper.class)) {
            BudgetDomain mapped = BudgetDomain.restore(
                    3L, 10L, 1,
                    new BigDecimal("500.00"),
                    BudgetStatus.PENDING_APPROVAL,
                    "Urgent request",
                    entity.getCreatedAt(),
                    entity.getEvaluatedAt()
            );
            mocked.when(() -> BudgetEntityMapper.toDomain(eq(entity))).thenReturn(mapped);

            BudgetDomain result = gateway.findById(3L);

            assertThat(result).isNotNull();
            assertThat(result.getIdentifier()).isEqualTo(3L);
            assertThat(result.getServiceOrderId()).isEqualTo(10L);
            assertThat(result.getStatus()).isEqualTo(BudgetStatus.PENDING_APPROVAL);

            verify(repository).findById(3L);
        }
    }

    @Test
    void testFindById_whenNotFound_thenReturnsNull() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        BudgetDomain result = gateway.findById(99L);

        assertThat(result).isNull();
        verify(repository).findById(99L);
    }

    @Test
    void testSave_whenOk_mapsToEntity_persists_andMapsBackToDomain() {
        // Domain de entrada
        BudgetDomain toSave = BudgetDomain.create(
                10L,
                new BigDecimal("500.00"),
                "Urgent request"
        );

        // Entity que o mapper deve gerar a partir do domain
        BudgetEntity mappedEntity = new BudgetEntity();
        mappedEntity.setBudgetId(null);
        mappedEntity.setServiceOrderId(10L);
        mappedEntity.setVersion(null);
        mappedEntity.setTotalAmount(new BigDecimal("500.00"));
        mappedEntity.setStatus(BudgetStatus.PENDING_APPROVAL);
        mappedEntity.setNotes("Urgent request");
        mappedEntity.setCreatedAt(LocalDateTime.now());
        mappedEntity.setEvaluatedAt(null);

        // Entity salva pelo repositório (com ID/versão)
        BudgetEntity savedEntity = new BudgetEntity();
        savedEntity.setBudgetId(3L);
        savedEntity.setServiceOrderId(10L);
        savedEntity.setVersion(1);
        savedEntity.setTotalAmount(new BigDecimal("500.00"));
        savedEntity.setStatus(BudgetStatus.PENDING_APPROVAL);
        savedEntity.setNotes("Urgent request");
        savedEntity.setCreatedAt(LocalDateTime.now().minusDays(1));
        savedEntity.setEvaluatedAt(null);

        // Domain final após mapeamento de volta
        BudgetDomain mappedBack = BudgetDomain.restore(
                3L, 10L, 1,
                new BigDecimal("500.00"),
                BudgetStatus.PENDING_APPROVAL,
                "Urgent request",
                savedEntity.getCreatedAt(),
                null
        );

        try (MockedStatic<BudgetEntityMapper> mocked = mockStatic(BudgetEntityMapper.class)) {
            mocked.when(() -> BudgetEntityMapper.toEntity(eq(toSave))).thenReturn(mappedEntity);
            when(repository.save(eq(mappedEntity))).thenReturn(savedEntity);
            mocked.when(() -> BudgetEntityMapper.toDomain(eq(savedEntity))).thenReturn(mappedBack);

            BudgetDomain result = gateway.save(toSave);

            Assertions.assertThat(result).isNotNull();
            Assertions.assertThat(result.getIdentifier()).isEqualTo(3L);
            Assertions.assertThat(result.getServiceOrderId()).isEqualTo(10L);
            Assertions.assertThat(result.getTotalAmount()).isEqualByComparingTo("500.00");
            Assertions.assertThat(result.getStatus()).isEqualTo(BudgetStatus.PENDING_APPROVAL);

            verify(repository).save(mappedEntity);
            // Verificações do mapper estático
            mocked.verify(() -> BudgetEntityMapper.toEntity(eq(toSave)));
            mocked.verify(() -> BudgetEntityMapper.toDomain(eq(savedEntity)));
        }
    }
}