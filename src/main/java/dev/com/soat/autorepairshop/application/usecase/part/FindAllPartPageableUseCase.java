package dev.com.soat.autorepairshop.application.usecase.part;

import dev.com.soat.autorepairshop.application.mapper.PartApplicationMapper;
import dev.com.soat.autorepairshop.application.models.output.PartOutputDTO;
import dev.com.soat.autorepairshop.domain.enums.SortDirectionType;
import dev.com.soat.autorepairshop.domain.exception.template.GenericException;
import dev.com.soat.autorepairshop.domain.gateway.PartGateway;
import dev.com.soat.autorepairshop.domain.utils.PageInfoGenericUtils;
import dev.com.soat.autorepairshop.domain.utils.PaginationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class FindAllPartPageableUseCase {

    private final PartGateway gateway;

    public PageInfoGenericUtils<PartOutputDTO> execute(final int page, final int size, final String sortBy, final String direction) {
        try {
            log.info("c=FindAllPartPageableUseCase m=execute s=start page={} size={}", page, size);

            final var pagination = new PaginationUtils(
                    page,
                    size,
                    sortBy,
                    SortDirectionType.valueOf(direction.toUpperCase())
            );

            final var pageInfo = gateway.findAllByPage(pagination);

            if (pageInfo.content().isEmpty()) {
                return new PageInfoGenericUtils<>(
                        Collections.emptyList(),
                        page,
                        size,
                        0,
                        0
                );
            }

            final var parts = pageInfo.content()
                    .stream()
                    .map(PartApplicationMapper::toDTO)
                    .toList();

            final var result = new PageInfoGenericUtils<>(
                    parts,
                    pageInfo.pageNumber(),
                    pageInfo.pageSize(),
                    pageInfo.totalElements(),
                    pageInfo.totalPages()
            );

            log.info("c=FindAllPartPageableUseCase m=execute s=done totalElements={}", result.totalElements());
            return result;
        } catch (Exception ex) {
            log.error("c=FindAllPartPageableUseCase m=execute s=generic error message={}", ex.getMessage());
            throw new GenericException("generic.error");
        }
    }
}
