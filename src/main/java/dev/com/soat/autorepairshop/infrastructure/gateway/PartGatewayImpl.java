package dev.com.soat.autorepairshop.infrastructure.gateway;

import dev.com.soat.autorepairshop.domain.entity.PartDomain;
import dev.com.soat.autorepairshop.domain.enums.SortDirectionType;
import dev.com.soat.autorepairshop.domain.gateway.PartGateway;
import dev.com.soat.autorepairshop.domain.utils.PageInfoGenericUtils;
import dev.com.soat.autorepairshop.domain.utils.PaginationUtils;
import dev.com.soat.autorepairshop.infrastructure.repository.PartRepository;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.PartEntity;
import dev.com.soat.autorepairshop.infrastructure.repository.mapper.PartEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PartGatewayImpl implements PartGateway {

    private final PartRepository repository;

    @Override
    public PartDomain save(final PartDomain part) {
        final var entity = PartEntityMapper.toEntity(part);
        final var saved = repository.save(entity);
        return PartEntityMapper.toDomain(saved);
    }

    @Override
    public PartDomain update(final PartDomain oldPartDomain, final PartDomain newPartDomain) {
        var entity = PartEntityMapper.toEntity(newPartDomain);

        entity.setPartId(oldPartDomain.getIdentifier());
        entity.setActive(oldPartDomain.getActive());
        entity.setCreatedAt(oldPartDomain.getCreatedAt());
        entity.setUpdatedAt(LocalDateTime.now());

        final var saved = repository.save(entity);

        return PartEntityMapper.toDomain(saved);
    }

    @Override
    public PartDomain findBySku(final String sku) {
        final var entity = repository.findBySku(sku).orElse(null);
        return entity != null ? PartEntityMapper.toDomain(entity) : null;
    }

    @Override
    public PartDomain findById(Long id) {
        final var entity = repository.findById(id).orElse(null);
        return entity != null ? PartEntityMapper.toDomain(entity) : null;
    }

    @Override
    public void delete(final Long identifier) {
        repository.findById(identifier).ifPresent(partEntity -> {
            partEntity.setActive(false);
            repository.save(partEntity);
        });
    }

    @Override
    public PageInfoGenericUtils<PartDomain> findAllByPage(PaginationUtils pagination) {
        final Sort.Direction springDirection = pagination.direction() == SortDirectionType.ASC ?
                Sort.Direction.ASC : Sort.Direction.DESC;

        final Pageable pageable = PageRequest.of(
                pagination.pageNumber(),
                pagination.pageSize(),
                Sort.by(springDirection, pagination.sortBy())
        );

        final Page<PartEntity> pageResult = repository.findAll(pageable);

        final List<PartDomain> parts = pageResult.getContent()
                .stream()
                .map(PartEntityMapper::toDomain)
                .toList();

        return new PageInfoGenericUtils<>(
                parts,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );
    }
}

