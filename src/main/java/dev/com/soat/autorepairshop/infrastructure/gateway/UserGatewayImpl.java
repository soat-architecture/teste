package dev.com.soat.autorepairshop.infrastructure.gateway;

import dev.com.soat.autorepairshop.domain.entity.UserDomain;
import dev.com.soat.autorepairshop.domain.enums.SortDirectionType;
import dev.com.soat.autorepairshop.domain.gateway.UserGateway;
import dev.com.soat.autorepairshop.domain.objects.Document;
import dev.com.soat.autorepairshop.domain.utils.PageInfoGenericUtils;
import dev.com.soat.autorepairshop.domain.utils.PaginationUtils;
import dev.com.soat.autorepairshop.infrastructure.repository.RoleRepository;
import dev.com.soat.autorepairshop.infrastructure.repository.UserRepository;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.UserEntity;
import dev.com.soat.autorepairshop.infrastructure.repository.mapper.UserEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserGatewayImpl implements UserGateway {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    public UserDomain save(UserDomain domain) {
        final var role = roleRepository.findByName(domain.getRole());
        final var entity = UserEntityMapper.toEntity(domain, role);
        final var saved = userRepository.save(entity);
        return UserEntityMapper.toDomain(saved);
    }

    @Override
    public UserDomain findByEmail(final String email) {
        final var entity = userRepository.findByEmail(email).orElse(null);
        return entity != null ? UserEntityMapper.toDomain(entity) : null;
    }

    @Override
    public UserDomain findByUserId(final Long userId) {
        final var entity = userRepository.findById(userId).orElse(null);
        return entity != null ? UserEntityMapper.toDomain(entity) : null;
    }

    @Override
    public UserDomain findByDocument(String document) {
        final var entity = userRepository.findByDocument(Document.from(document).unformat()).orElse(null);
        return entity != null ? UserEntityMapper.toDomain(entity) : null;
    }

    @Override
    public Optional<UserDomain> findEmployeeById(Long employeeId) {
        var employee = this.userRepository.findEmployeeById(employeeId);
        return employee.map(UserEntityMapper::toDomain);
    }

    @Override
    public PageInfoGenericUtils<UserDomain> findAllByPage(final PaginationUtils pagination) {
        final Sort.Direction springDirection = pagination.direction() == SortDirectionType.ASC ?
                Sort.Direction.ASC : Sort.Direction.DESC;

        final Pageable pageable = PageRequest.of(
                pagination.pageNumber(),
                pagination.pageSize(),
                Sort.by(springDirection, pagination.sortBy())
        );

        final Page<UserEntity> pageResult = userRepository.findAll(pageable);

        final List<UserDomain> users = pageResult.getContent()
                .stream()
                .map(UserEntityMapper::toDomain)
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
