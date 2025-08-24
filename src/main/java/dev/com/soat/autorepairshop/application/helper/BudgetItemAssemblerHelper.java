package dev.com.soat.autorepairshop.application.helper;

import dev.com.soat.autorepairshop.application.mapper.BudgetItemApplicationMapper;
import dev.com.soat.autorepairshop.domain.entity.BudgetItemDomain;
import dev.com.soat.autorepairshop.domain.enums.ItemTypeEnum;
import dev.com.soat.autorepairshop.domain.gateway.PartGateway;
import dev.com.soat.autorepairshop.domain.gateway.ServiceGateway;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.BudgetItemRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BudgetItemAssemblerHelper {

    private final PartGateway partGateway;
    private final ServiceGateway serviceGateway;

    public List<BudgetItemDomain> assemble(List<BudgetItemRequestDTO> items, Long budgetId) {
        return items.stream()
                .map(item -> {
                    BigDecimal unitPrice;
                    if (item.type() == ItemTypeEnum.PART){
                        unitPrice = partGateway.findById(item.id()).getSellingPrice();
                    } else {
                        unitPrice = serviceGateway.findById(item.id()).getBasePrice();
                    }
                    return BudgetItemApplicationMapper.toDomain(item, budgetId, unitPrice);
                })
                .toList();
    }
}