package dev.com.soat.autorepairshop.domain.gateway;

import dev.com.soat.autorepairshop.domain.entity.PartDomain;
import dev.com.soat.autorepairshop.domain.utils.PageInfoGenericUtils;
import dev.com.soat.autorepairshop.domain.utils.PaginationUtils;

public interface PartGateway {

    PartDomain save(final PartDomain part);
    PartDomain update(PartDomain existingPart, PartDomain newPart);
    PartDomain findBySku(final String sku);
    PartDomain findById(final Long id);
    void delete(final Long identifier);
    PageInfoGenericUtils<PartDomain> findAllByPage(final PaginationUtils pagination);
}
