package dev.com.soat.autorepairshop.infrastructure.gateway;

import dev.com.soat.autorepairshop.domain.entity.PartDomain;
import dev.com.soat.autorepairshop.domain.enums.SortDirectionType;
import dev.com.soat.autorepairshop.domain.utils.PageInfoGenericUtils;
import dev.com.soat.autorepairshop.domain.utils.PaginationUtils;
import dev.com.soat.autorepairshop.infrastructure.repository.PartRepository;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.PartEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartGatewayImplTest {

    @InjectMocks
    private PartGatewayImpl gateway;

    @Mock
    private PartRepository repository;

    private PartDomain domain;
    private PartEntity entity;

    @BeforeEach
    void setUp() {
        domain = new PartDomain(
                1L,
                "Part Name",
                "SKU-123",
                "Part description",
                "BrandX",
                new BigDecimal("100.00"),
                new BigDecimal("80.00"),
                true,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        );

        entity = new PartEntity(
                1L,
                domain.getName(),
                domain.getSku(),
                domain.getDescription(),
                domain.getBrand(),
                domain.getSellingPrice(),
                domain.getBuyPrice(),
                domain.getActive(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }

    @Test
    void testSave() {
        when(repository.save(any(PartEntity.class))).thenReturn(entity);

        PartDomain savedPart = gateway.save(domain);

        assertThat(savedPart).isNotNull();
        assertThat(savedPart.getName()).isEqualTo(domain.getName());
        verify(repository).save(any(PartEntity.class));
    }

    @Test
    void testUpdate() {
        when(repository.save(any(PartEntity.class))).thenReturn(entity);

        PartDomain updatedPart = gateway.update(domain, domain);

        assertThat(updatedPart).isNotNull();
        assertThat(updatedPart.getSku()).isEqualTo(domain.getSku());
        verify(repository).save(any(PartEntity.class));
    }

    @Test
    void testFindBySku() {
        when(repository.findBySku("SKU-123")).thenReturn(Optional.of(entity));

        PartDomain foundPart = gateway.findBySku("SKU-123");

        assertThat(foundPart).isNotNull();
        assertThat(foundPart.getSku()).isEqualTo("SKU-123");
        verify(repository).findBySku("SKU-123");
    }

    @Test
    void testFindById() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        PartDomain foundPart = gateway.findById(1L);

        assertThat(foundPart).isNotNull();
        assertThat(foundPart.getIdentifier()).isEqualTo(1L);
        verify(repository).findById(1L);
    }

    @Test
    void testDelete() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        gateway.delete(1L);

        assertThat(entity.getActive()).isFalse();
        verify(repository).save(any(PartEntity.class));
    }

    @Test
    void testFindAllByPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<PartEntity> page = new PageImpl<>(Collections.singletonList(entity), pageable, 1);

        when(repository.findAll(any(Pageable.class))).thenReturn(page);

        var pagination = new PaginationUtils(0, 10, "name", SortDirectionType.ASC);
        PageInfoGenericUtils<PartDomain> result = gateway.findAllByPage(pagination);

        assertThat(result).isNotNull();
        verify(repository).findAll(any(Pageable.class));
    }
}
