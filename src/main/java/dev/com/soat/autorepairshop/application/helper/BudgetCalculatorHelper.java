package dev.com.soat.autorepairshop.application.helper;

import dev.com.soat.autorepairshop.domain.exception.template.ConflictException;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.gateway.PartGateway;
import dev.com.soat.autorepairshop.domain.gateway.PartInventoryGateway;
import dev.com.soat.autorepairshop.domain.gateway.ServiceGateway;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.BudgetItemRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class BudgetCalculatorHelper {

    private final PartGateway partGateway;
    private final ServiceGateway serviceGateway;
    private final PartInventoryGateway partInventoryGateway;

    public BigDecimal calculateTotal(List<BudgetItemRequestDTO> items) {
        if (items == null || items.isEmpty()) return BigDecimal.ZERO;

        return items.stream()
                .map(this::priceFor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal priceFor(BudgetItemRequestDTO item) {
        if (item.type() == null) {
            throw new DomainException("item.type.required.id", item.id());
        }
        if (item.quantity() == null || item.quantity() <= 0) {
            throw new DomainException("item.quantity.invalid.id", item.id());
        }

        switch (item.type()) {
            case PART -> {
                return calculatePartPrice(item);
            }
            case SERVICE -> {
                final var service = serviceGateway.findById(item.id());
                if (service == null) throw new NotFoundException("service.not.found.id", item.id());

                BigDecimal total = service.getBasePrice().multiply(BigDecimal.valueOf(item.quantity()));
                log.debug("SERVICE calc: id={} qty={} base={} total={}", item.id(), item.quantity(), service.getBasePrice(), total);
                return total;
            }
            default -> throw new ConflictException("item.type.unsupported", item.type());
        }
    }

    private BigDecimal calculatePartPrice(BudgetItemRequestDTO item) {
        final var part = partGateway.findById(item.id());
        if (part == null)
            throw new NotFoundException("part.not.found.id", item.id());
        if (!part.getActive()) {
            throw new ConflictException("part.inactive.id", item.id());
        }

        // quantidade de peça deve ser inteira
        if (item.quantity() % 1 != 0) {
            throw new DomainException("part.quantity.must.be.integer.id", item.id());
        }
        int qty = item.quantity().intValue();

        // checa estoque disponível (sem dar baixa aqui)
        Integer onHand = partInventoryGateway.getOnHand(item.id());
        if (onHand == null) {
            throw new NotFoundException("part.inventory.not.found.id", item.id());
        }
        if (onHand < qty) {
            throw new ConflictException("insufficient.stock.partId", item.id());
        }

        BigDecimal total = part.getSellingPrice().multiply(BigDecimal.valueOf(qty));
        log.debug("PART calc: id={} qty={} unit={} total={}", item.id(), qty, part.getSellingPrice(), total);
        return total;
    }
}
