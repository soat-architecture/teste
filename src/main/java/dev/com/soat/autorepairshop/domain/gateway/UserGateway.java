package dev.com.soat.autorepairshop.domain.gateway;

import dev.com.soat.autorepairshop.domain.entity.UserDomain;
import dev.com.soat.autorepairshop.domain.utils.PageInfoGenericUtils;
import dev.com.soat.autorepairshop.domain.utils.PaginationUtils;

import java.util.Optional;

public interface UserGateway {
    UserDomain save(final UserDomain userDomain);
    UserDomain findByEmail(final String email);
    UserDomain findByUserId(final Long userId);
    UserDomain findByDocument(final String document);
    Optional<UserDomain> findEmployeeById(final Long employeeId);
    PageInfoGenericUtils<UserDomain> findAllByPage(final PaginationUtils pagination);
}
