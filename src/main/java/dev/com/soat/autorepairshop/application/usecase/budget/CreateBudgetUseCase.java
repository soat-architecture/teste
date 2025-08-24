package dev.com.soat.autorepairshop.application.usecase.budget;

import dev.com.soat.autorepairshop.application.helper.BudgetCalculatorHelper;
import dev.com.soat.autorepairshop.application.helper.BudgetItemAssemblerHelper;
import dev.com.soat.autorepairshop.application.helper.PartInventoryConsumptionHelper;
import dev.com.soat.autorepairshop.application.mapper.BudgetApplicationMapper;
import dev.com.soat.autorepairshop.application.models.input.BudgetInputDTO;
import dev.com.soat.autorepairshop.domain.entity.OrderDomain;
import dev.com.soat.autorepairshop.domain.enums.OrderStatusType;
import dev.com.soat.autorepairshop.application.utils.UserValidationUtils;
import dev.com.soat.autorepairshop.domain.entity.BudgetDomain;
import dev.com.soat.autorepairshop.domain.entity.BudgetItemDomain;
import dev.com.soat.autorepairshop.domain.exception.template.ConflictException;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.gateway.BudgetGateway;
import dev.com.soat.autorepairshop.domain.gateway.OrderGateway;
import dev.com.soat.autorepairshop.domain.gateway.BudgetItemGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Transactional
@Component
@Slf4j
@RequiredArgsConstructor
public class CreateBudgetUseCase {

    private final OrderGateway orderGateway;
    private final BudgetGateway budgetGateway;
    private final BudgetItemGateway budgetItemGateway;
    private final BudgetCalculatorHelper budgetCalculatorHelper;
    private final BudgetItemAssemblerHelper budgetItemAssemblerHelper;
    private final PartInventoryConsumptionHelper partInventoryConsumptionHelper;
    private final UserValidationUtils userValidationUtils;

    @Transactional
    public void execute(BudgetInputDTO dto) {
        log.info("c=CreateBudgetUseCase m=execute s=start serviceOrderId={}", dto.serviceOrderId());

        // 1) valida funcionário
        userValidationUtils.validateUserExistenceById(dto.employeeId());

        // 2) não pode existir orçamento prévio para a mesma OS
        long countForOs = budgetGateway.countByServiceOrderId(dto.serviceOrderId());
        if (countForOs > 0) {
            throw new ConflictException("budget.already.exists.for.service.order");
        }

        // 3) OS deve existir e estar em diagnóstico
        final OrderDomain existingOrder = orderGateway.findById(dto.serviceOrderId())
                .orElseThrow(() -> new NotFoundException("service.order.not.found"));

        if (existingOrder.getStatus() != OrderStatusType.EM_DIAGNOSTICO) {
            throw new DomainException("order.status.must.be.diagnostic");
        }

        // 4) versão sempre 1 no create
        final int version = 1;

        // 5) calcula total (PART usa sellingPrice; SERVICE usa basePrice)
        final var totalAmount = budgetCalculatorHelper.calculateTotal(dto.items());
        final var now = LocalDateTime.now();

        // 6) persiste orçamento
        final var domain = BudgetApplicationMapper.toDomainForCreate(dto, totalAmount, version, now);
        final BudgetDomain saved = budgetGateway.save(domain);

        // 7) persiste itens
        final List<BudgetItemDomain> budgetItems =
                budgetItemAssemblerHelper.assemble(dto.items(), saved.getIdentifier());
        budgetItemGateway.saveAll(budgetItems);

        // 8) vincula orçamento ativo na OS
        existingOrder.attributeBudget(saved.getIdentifier());
        orderGateway.save(existingOrder);

        // 9) baixa estoque de PARTs e registra movimentos (atômico com a transação)
        partInventoryConsumptionHelper.consumeFor(
                dto.items(),
                dto.employeeId(),
                dto.serviceOrderId(),
                "BUDGET_CREATE " + saved.getIdentifier()
        );

        log.info("c=CreateBudgetUseCase m=execute s=success serviceOrderId={} budgetId={} version={}",
                dto.serviceOrderId(), saved.getIdentifier(), version);
    }
}
