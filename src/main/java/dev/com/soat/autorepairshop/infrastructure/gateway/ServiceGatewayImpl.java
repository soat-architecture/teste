package dev.com.soat.autorepairshop.infrastructure.gateway;

import dev.com.soat.autorepairshop.domain.entity.ServiceDomain;
import dev.com.soat.autorepairshop.domain.enums.SortDirectionType;
import dev.com.soat.autorepairshop.domain.gateway.ServiceGateway;
import dev.com.soat.autorepairshop.domain.utils.PageInfoGenericUtils;
import dev.com.soat.autorepairshop.domain.utils.PaginationUtils;
import dev.com.soat.autorepairshop.infrastructure.repository.ServiceRepository;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.ServiceEntity;
import dev.com.soat.autorepairshop.infrastructure.repository.mapper.ServiceEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ServiceGatewayImpl implements ServiceGateway {

    private final ServiceRepository repository;
    private final ServiceRepository serviceRepository;

    @Override
    public ServiceDomain save(ServiceDomain service) {
        final var entity = ServiceEntityMapper.toEntity(service);
        final var saved = repository.save(entity);
        return ServiceEntityMapper.toDomain(saved);
    }

    @Override
    public ServiceDomain update(ServiceDomain existingService, ServiceDomain newService) {
        final var entity = ServiceEntityMapper.toEntity(existingService.getIdentifier(), newService);
        final var saved = repository.save(entity);
        return ServiceEntityMapper.toDomain(saved);
    }

    @Override
    public ServiceDomain findById(Long id) {
        final var entity = serviceRepository.findById(id).orElse(null);
        return entity != null ? ServiceEntityMapper.toDomain(entity) : null;
    }

    @Override
    public void delete(Long identifier) {
        final var entity = serviceRepository.findById(identifier).orElse(null);
        if (entity != null) {
            serviceRepository.delete(entity);
        }
    }

    @Override
    public PageInfoGenericUtils<ServiceDomain> findAllByPage(PaginationUtils pagination) {
        final Sort.Direction springDirection = pagination.direction() == SortDirectionType.ASC ?
                Sort.Direction.ASC : Sort.Direction.DESC;

        final Pageable pageable = PageRequest.of(
                pagination.pageNumber(),
                pagination.pageSize(),
                Sort.by(springDirection, pagination.sortBy())
        );

        final Page<ServiceEntity> pageResult = serviceRepository.findAll(pageable);

        final List<ServiceDomain> users = pageResult.getContent()
                .stream()
                .map(ServiceEntityMapper::toDomain)
                .toList();

        return new PageInfoGenericUtils<>(
                users,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );
    }
}
