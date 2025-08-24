package dev.com.soat.autorepairshop.application.usecase.part;

import dev.com.soat.autorepairshop.application.mapper.PartApplicationMapper;
import dev.com.soat.autorepairshop.application.models.input.PartInputDTO;
import dev.com.soat.autorepairshop.domain.entity.PartDomain;
import dev.com.soat.autorepairshop.domain.exception.template.*;
import dev.com.soat.autorepairshop.domain.gateway.PartGateway;
import dev.com.soat.autorepairshop.infrastructure.api.mapper.PartMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@Slf4j
@RequiredArgsConstructor
public class UpdatePartUseCase {

    private final PartGateway gateway;

    public void execute(Long id, PartInputDTO dto) {
        try {
            log.info("c=UpdatePartUseCase m=execute s=start id={} sku={}", id, dto.sku());

            final var oldPartDomain = gateway.findById(id);
            if (oldPartDomain == null) {
                throw new NotFoundException("part.not.found");
            }

            if (!oldPartDomain.getActive()) {
                throw new ValidatorException("part.inactive");
            }

            if (!oldPartDomain.getSku().equals(dto.sku())) {
                final var conflict = gateway.findBySku(dto.sku());
                if (conflict != null && !conflict.getIdentifier().equals(id)) {
                    throw new ConflictException("part.sku.already.exists");
                }
            }

            final var newPartDomain = PartApplicationMapper.toDomain(dto);
            gateway.update(oldPartDomain, newPartDomain);

            log.info("c=UpdatePartUseCase m=execute s=success id={} sku={}", id, newPartDomain.getSku());
        } catch (NotFoundException ex) {
            log.error("c=UpdatePartUseCase m=execute s=not-found message={} id={} sku={}", ex.getMessage(), id, dto.sku());
            throw ex;
        } catch (ValidatorException ex) {
            log.error("c=UpdatePartUseCase m=execute s=validation error message={} id={} sku={}", ex.getMessage(), id, dto.sku());
            throw ex;
        } catch (ConflictException ex) {
            log.error("c=UpdatePartUseCase m=execute s=conflict error message={} id={} sku={}", ex.getMessage(), id, dto.sku());
            throw ex;
        } catch (DomainException ex) {
            log.error("c=UpdatePartUseCase m=execute s=domain error message={} id={} sku={}", ex.getMessage(), id, dto.sku());
            throw ex;
        } catch (Exception ex) {
            log.error("c=UpdatePartUseCase m=execute s=generic error message={} id={} sku={}", ex.getMessage(), id, dto.sku());
            throw new GenericException("generic.error");
        }
    }
}

