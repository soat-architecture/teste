package dev.com.soat.autorepairshop.application.mapper;

import dev.com.soat.autorepairshop.domain.entity.OrderDomain;
import dev.com.soat.autorepairshop.domain.entity.OrderHistoryDomain;

public class ApplicationOrderHistoryMapper {

    private ApplicationOrderHistoryMapper(){

    }

    public static OrderHistoryDomain map(OrderDomain domain) {
        return OrderHistoryDomain.create(domain.getIdentifier(), domain.getStatus(), domain.getNotes());
    }

}
