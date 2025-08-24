package dev.com.soat.autorepairshop.domain.utils;

import java.util.List;

public record PageInfoGenericUtils<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages
) {
}
