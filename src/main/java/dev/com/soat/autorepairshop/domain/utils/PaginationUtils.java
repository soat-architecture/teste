package dev.com.soat.autorepairshop.domain.utils;

import dev.com.soat.autorepairshop.domain.enums.SortDirectionType;

public record PaginationUtils(
        int pageNumber,
        int pageSize,
        String sortBy,
        SortDirectionType direction
) {
}
