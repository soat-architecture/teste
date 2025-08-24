package dev.com.soat.autorepairshop.application.usecase.user;

import dev.com.soat.autorepairshop.application.mapper.UserApplicationMapper;
import dev.com.soat.autorepairshop.application.models.output.UserOutputDTO;
import dev.com.soat.autorepairshop.domain.enums.SortDirectionType;
import dev.com.soat.autorepairshop.domain.exception.template.GenericException;
import dev.com.soat.autorepairshop.domain.gateway.UserGateway;
import dev.com.soat.autorepairshop.domain.utils.PageInfoGenericUtils;
import dev.com.soat.autorepairshop.domain.utils.PaginationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class FindAllUsersPageableUseCase {
    private final UserGateway userGateway;

    public PageInfoGenericUtils<UserOutputDTO> execute(final int page, final int size, final String sortBy, final String direction) {
        try {
            log.info("c=FindAllUsersPageableUseCase m=execute s=start page={} size={}", page, size);
            final var pagination = new PaginationUtils(
                    page,
                    size,
                    sortBy,
                    SortDirectionType.valueOf(direction.toUpperCase())
            );

            final var pageInfo = userGateway.findAllByPage(pagination);

            if (pageInfo.content().isEmpty()) {
                return new PageInfoGenericUtils<>(
                        Collections.emptyList(),
                        page,
                        size,
                        0,
                        0
                );
            }

            final var users = pageInfo.content()
                    .stream()
                    .map(UserApplicationMapper::toDTO)
                    .toList();

            var result = new PageInfoGenericUtils<>(
                    users,
                    pageInfo.pageNumber(),
                    pageInfo.pageSize(),
                    pageInfo.totalElements(),
                    pageInfo.totalPages()
            );

            log.info("c=FindAllUsersPageableUseCase m=execute s=done totalElements={}", result.totalElements());
            return result;
        } catch (Exception ex) {
            log.error("c=FindAllUsersPageableUseCase m=execute s=generic error message={}", ex.getMessage());
            throw new GenericException("generic.error");
        }
    }
}
