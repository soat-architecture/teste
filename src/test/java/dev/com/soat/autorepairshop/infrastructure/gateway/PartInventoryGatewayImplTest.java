package dev.com.soat.autorepairshop.infrastructure.gateway;

import dev.com.soat.autorepairshop.infrastructure.repository.PartInventoryRepository;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.PartInventoryEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartInventoryGatewayImplTest {

    @InjectMocks
    private PartInventoryGatewayImpl gateway;

    @Mock
    private PartInventoryRepository repository;

    @Test
    void getOnHand_returnsValueFromRepository() {
        when(repository.findOnHand(10L)).thenReturn(7);

        Integer result = gateway.getOnHand(10L);

        assertEquals(7, result);
        verify(repository).findOnHand(10L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void decrease_whenRowUpdated_ok() {
        when(repository.tryDecrease(5L, 3)).thenReturn(1);

        assertDoesNotThrow(() -> gateway.decrease(5L, 3));

        verify(repository).tryDecrease(5L, 3);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void decrease_whenNoRowUpdated_throwsIllegalState() {
        when(repository.tryDecrease(99L, 2)).thenReturn(0);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> gateway.decrease(99L, 2));

        assertEquals("insufficient.stock.partId=99", ex.getMessage());
        verify(repository).tryDecrease(99L, 2);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void increase_whenTryIncreaseUpdatesRow_returnsEarly() {
        when(repository.tryIncrease(1L, 4)).thenReturn(1);

        assertDoesNotThrow(() -> gateway.increase(1L, 4));

        verify(repository).tryIncrease(1L, 4);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void increase_whenTryIncreaseReturnsZero_thenSaveSucceeds() {
        when(repository.tryIncrease(2L, 5)).thenReturn(0);
        // save sem exceção
        when(repository.save(any(PartInventoryEntity.class)))
                .thenAnswer(inv -> {
                    PartInventoryEntity e = inv.getArgument(0);
                    // sanity: campos básicos preenchidos
                    assertEquals(2L, e.getPartId());
                    assertEquals(5, e.getQuantityOnHand());
                    assertNotNull(e.getCreatedAt());
                    assertNotNull(e.getUpdatedAt());
                    return e;
                });

        assertDoesNotThrow(() -> gateway.increase(2L, 5));

        verify(repository).tryIncrease(2L, 5);
        verify(repository).save(any(PartInventoryEntity.class));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void increase_whenSaveThrowsConstraint_thenRetryTryIncreaseSucceeds() {
        // 1ª chamada retorna 0 → vai tentar salvar; 2ª chamada retorna 1 → retry ok
        when(repository.tryIncrease(3L, 6)).thenReturn(0, 1);

        when(repository.save(any(PartInventoryEntity.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate key"));

        assertDoesNotThrow(() -> gateway.increase(3L, 6));

        InOrder inOrder = inOrder(repository);
        inOrder.verify(repository).tryIncrease(3L, 6);                 // 1ª tentativa (0)
        inOrder.verify(repository).save(any(PartInventoryEntity.class)); // save que explode
        inOrder.verify(repository).tryIncrease(3L, 6);                 // retry (1)
        verifyNoMoreInteractions(repository);
    }

    @Test
    void increase_whenSaveThrowsConstraint_andRetryFails_thenRethrow() {
        when(repository.tryIncrease(4L, 8)).thenReturn(0, 0); // 1ª=0, retry=0
        DataIntegrityViolationException dive = new DataIntegrityViolationException("duplicate key");
        when(repository.save(any(PartInventoryEntity.class))).thenThrow(dive);

        DataIntegrityViolationException ex = assertThrows(DataIntegrityViolationException.class,
                () -> gateway.increase(4L, 8));
        assertSame(dive, ex);

        InOrder inOrder = inOrder(repository);
        inOrder.verify(repository).tryIncrease(4L, 8);                 // 1ª (0)
        inOrder.verify(repository).save(any(PartInventoryEntity.class)); // explode
        inOrder.verify(repository).tryIncrease(4L, 8);                 // retry (0)
        verifyNoMoreInteractions(repository);
    }
}

