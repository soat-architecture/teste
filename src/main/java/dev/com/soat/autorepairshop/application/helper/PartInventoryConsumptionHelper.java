package dev.com.soat.autorepairshop.application.helper;

import dev.com.soat.autorepairshop.application.mapper.InventoryMovementApplicationMapper;
import dev.com.soat.autorepairshop.domain.entity.BudgetItemDomain;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.gateway.InventoryMovementGateway;
import dev.com.soat.autorepairshop.domain.gateway.PartInventoryGateway;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.BudgetItemRequestDTO;
import dev.com.soat.autorepairshop.domain.enums.ItemTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartInventoryConsumptionHelper {

    private final PartInventoryGateway inventory;
    private final InventoryMovementGateway movementGateway;

    @Transactional
    public void consumeFor(List<BudgetItemRequestDTO> items,
                           Long userId,
                           Long serviceOrderId,
                           String reason) {
        if (items == null || items.isEmpty()) return;

        for (var it : items) {
            if (it.type() != ItemTypeEnum.PART) continue;

            int qty = validateIntegerQtyOrThrow(it);
            final Long partId = it.id();

            int before = inventory.getOnHand(partId);
            if (before < qty) {
                throw new DomainException("inventory.insufficient.part.id", partId);
            }

            inventory.decrease(partId, qty);

            var movement = InventoryMovementApplicationMapper.outboundSale(
                    partId, userId, serviceOrderId, qty, before, reason
            );
            movementGateway.save(movement);

            log.debug("Inventory consumed: partId={} before={} qty={} after={} soId={} userId={}",
                    partId, before, qty, before - qty, serviceOrderId, userId);
        }
    }


    @Transactional
    public void restoreFor(List<BudgetItemDomain> oldItems,
                                   Long userId,
                                   Long serviceOrderId,
                                   String reason) {
        if (oldItems == null || oldItems.isEmpty()) return;

        for (var it : oldItems) {
            int qty = it.getQuantity();
            if (it.getItemType() != ItemTypeEnum.PART || qty <= 0) continue;

            final Long partId = it.getPartId();

            int before = inventory.getOnHand(partId);
            inventory.increase(partId, qty);
            int after = before + qty;

            var movement = InventoryMovementApplicationMapper.adjustmentIncrease(
                    partId, userId, serviceOrderId, qty, before, "restore.previous.budget.items"
            );
            movementGateway.save(movement);

            log.debug("Inventory restored: partId={} before={} qty={} after={} soId={} userId={}",
                    partId, before, qty, after, serviceOrderId, userId);
        }
    }

    private int validateIntegerQtyOrThrow(BudgetItemRequestDTO it) {
        if (it.quantity() == null) {
            throw new DomainException("part.quantity.required.id", it.id());
        }
        double q = it.quantity();
        if (q <= 0 || (q % 1) != 0) {
            throw new DomainException("part.quantity.must.be.integer.id", it.id());
        }
        return (int) q;
    }
}
