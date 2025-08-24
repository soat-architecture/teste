package dev.com.soat.autorepairshop.application.usecase.budget;

import dev.com.soat.autorepairshop.application.helper.BudgetCalculatorHelper;
import dev.com.soat.autorepairshop.application.helper.BudgetItemAssemblerHelper;
import dev.com.soat.autorepairshop.application.helper.PartInventoryConsumptionHelper;
import dev.com.soat.autorepairshop.application.mapper.BudgetApplicationMapper;
import dev.com.soat.autorepairshop.application.models.input.BudgetInputDTO;
import dev.com.soat.autorepairshop.application.utils.UserValidationUtils;
import dev.com.soat.autorepairshop.domain.entity.BudgetDomain;
import dev.com.soat.autorepairshop.domain.entity.BudgetItemDomain;
import dev.com.soat.autorepairshop.domain.exception.template.ConflictException;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.gateway.BudgetGateway;
import dev.com.soat.autorepairshop.domain.gateway.BudgetItemGateway;
import dev.com.soat.autorepairshop.domain.gateway.OrderGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional
@Component
@Slf4j
@RequiredArgsConstructor
public class UpdateBudgetUseCase {

    private final OrderGateway orderGateway;
    private final BudgetGateway budgetGateway;
    private final BudgetItemGateway budgetItemGateway;
    private final BudgetCalculatorHelper budgetCalculatorHelper;
    private final BudgetItemAssemblerHelper budgetItemAssemblerHelper;
    private final UserValidationUtils userValidationUtils;
    private final PartInventoryConsumptionHelper partInventoryConsumptionHelper;

    @Transactional
    public void execute(BudgetInputDTO dto) {
        log.info("c=UpdateBudgetUseCase m=execute s=start budgetId={}", dto.budgetId());
        final var order = orderGateway.findById(dto.serviceOrderId())
                .orElseThrow(() -> new NotFoundException("order.not.found"));

        if (order.isOrderNotInDiagnostic()){
            throw new ConflictException("order.is.not.diagnostic");
        }

        if (dto.budgetId() == null) {
            throw new DomainException("budget.id.required");
        }

        // 1) carrega versão base e valida usuário
        final BudgetDomain base = budgetGateway.findById(dto.budgetId());
        if (base == null) {
            throw new NotFoundException("budget.not.found");
        }

        userValidationUtils.validateUserExistenceById(dto.employeeId());

        final Long serviceOrderId = base.getServiceOrderId();

        // 2) não permitir troca de OS
        if (dto.serviceOrderId() != null && !dto.serviceOrderId().equals(serviceOrderId)) {
            log.warn("Attempt to change serviceOrderId ignored: dto={}, base={}", dto.serviceOrderId(), serviceOrderId);
            throw new IllegalArgumentException("order.id.cannot.change");
        }

        // 3) buscar itens da versão anterior e ESTORNAR estoque de PARTs
        final List<BudgetItemDomain> previousItems = budgetItemGateway.findByBudgetId(base.getIdentifier());
        partInventoryConsumptionHelper.restoreFor(
                previousItems,
                dto.employeeId(),
                serviceOrderId,
                "budget.update.restore.previous.items"
        );

        // 4) calcular próxima versão e total dos NOVOS itens
        final long countForOs = budgetGateway.countByServiceOrderId(serviceOrderId);
        final int nextVersion = Math.toIntExact(countForOs + 1);

        final var totalAmount = budgetCalculatorHelper.calculateTotal(dto.items());

        // 5) criar nova versão (mantém createdAt do base; evaluatedAt = null; status PENDING_APPROVAL)
        final var newVersionDomain =
                BudgetApplicationMapper.toDomainForNewVersionFrom(base, dto, totalAmount, nextVersion);

        // 6) persistir nova versão + itens
        final BudgetDomain saved = budgetGateway.save(newVersionDomain);

        final List<BudgetItemDomain> newItems =
                budgetItemAssemblerHelper.assemble(dto.items(), saved.getIdentifier());
        budgetItemGateway.saveAll(newItems);

        // 7) consumir estoque conforme os NOVOS itens (PARTs)
        partInventoryConsumptionHelper.consumeFor(
                dto.items(),
                dto.employeeId(),
                serviceOrderId,
                "budget.update.consume.new.items"
        );

        // 8) tornar a nova versão o orçamento ativo da OS
        orderGateway.setActiveBudget(serviceOrderId, saved.getIdentifier());

        log.info("c=UpdateBudgetUseCase m=execute s=success budgetId={} version={}",
                saved.getIdentifier(), nextVersion);
    }
}
