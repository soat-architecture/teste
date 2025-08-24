package dev.com.soat.autorepairshop.domain.gateway;

import dev.com.soat.autorepairshop.domain.entity.OrderHistoryDomain;

import java.util.List;

public interface OrderHistoryGateway {

    void save(OrderHistoryDomain orderHistory);

    List<OrderHistoryDomain> findAllByOrderId(Long orderId);
}
