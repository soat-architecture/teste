package dev.com.soat.autorepairshop.application.usecase.part;


import dev.com.soat.autorepairshop.application.mapper.InventoryApplicationMapper;
import dev.com.soat.autorepairshop.application.mapper.PartApplicationMapper;
import dev.com.soat.autorepairshop.application.models.input.PartInputDTO;
import dev.com.soat.autorepairshop.domain.exception.template.ConflictException;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.exception.template.GenericException;
import dev.com.soat.autorepairshop.domain.gateway.InventoryGateway;
import dev.com.soat.autorepairshop.domain.gateway.PartGateway;
import dev.com.soat.autorepairshop.infrastructure.api.mapper.InventoryMovementMapper;
import dev.com.soat.autorepairshop.infrastructure.api.mapper.PartMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@Slf4j
@RequiredArgsConstructor
public class CreatePartUseCase {
    private final PartGateway gateway;
    private final InventoryGateway inventoryGateway;

    @Transactional
    public void execute(PartInputDTO dto) {
        try {
            log.info("c=CreatePartUseCase m=execute s=start sku={}", dto.sku());

            final var existingPart = gateway.findBySku(dto.sku());

            if (existingPart != null) {
                throw new ConflictException("part.already.exists");
            }
            final var domain = PartApplicationMapper.toDomain(dto);
            final var savedDomain = gateway.save(domain);
            final var inventory = InventoryApplicationMapper.mapToDomain(
                    savedDomain.getIdentifier()
            );
            inventoryGateway.saveNotReturns(inventory);
            log.info("c=CreatePartUseCase m=execute s=success sku={}", dto.sku());
        } catch (DomainException ex) {
            log.error("c=FindPartBySkuUseCase m=execute s=domain error message={} sku={}", ex.getMessage(), dto.sku());
            throw new DomainException(ex.getMessage());
        } catch (ConflictException ex){
            log.error("c=FindPartBySkuUseCase m=execute s=conflict error message={} sku={}", ex.getMessage(), dto.sku());
            throw new ConflictException(ex.getMessage());
        } catch (Exception ex) {
            log.error("c=FindPartBySkuUseCase m=execute s=generic error message={} sku={}", ex.getMessage(), dto.sku());
            throw new GenericException("generic.error");
        }
    }
}
