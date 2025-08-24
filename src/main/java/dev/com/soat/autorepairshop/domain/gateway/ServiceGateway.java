package dev.com.soat.autorepairshop.domain.gateway;

import dev.com.soat.autorepairshop.domain.entity.ServiceDomain;
import dev.com.soat.autorepairshop.domain.utils.PageInfoGenericUtils;
import dev.com.soat.autorepairshop.domain.utils.PaginationUtils;

public interface ServiceGateway {
    ServiceDomain save(final ServiceDomain service);
    ServiceDomain update(ServiceDomain existingService, ServiceDomain newService);
    ServiceDomain findById(final Long id);
    void delete(final Long identifier);
    PageInfoGenericUtils<ServiceDomain> findAllByPage(final PaginationUtils pagination);
}
