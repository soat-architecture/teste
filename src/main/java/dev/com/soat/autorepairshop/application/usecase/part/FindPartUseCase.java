package dev.com.soat.autorepairshop.application.usecase.part;

import dev.com.soat.autorepairshop.application.mapper.PartApplicationMapper;
import dev.com.soat.autorepairshop.application.models.output.PartOutputDTO;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.exception.template.GenericException;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.gateway.PartGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@Slf4j
@RequiredArgsConstructor
public class FindPartUseCase {

    private final PartGateway gateway;

    public PartOutputDTO execute(final Long identifier) {
        try {
            log.info("c=FindPartUseCase m=execute s=start partId={}", identifier);
            final var part = gateway.findById(identifier);
            if (part == null) {
                throw new NotFoundException("part.not.found");
            }
            log.info("c=FindPartUseCase m=execute s=success partId={}", identifier);
            return PartApplicationMapper.toDTO(part);
        } catch (DomainException ex) {
            log.error("c=FindPartByIdUseCase m=execute s=domain error message={} partId={}", ex.getMessage(), identifier);
            throw new DomainException(ex.getMessage());
        } catch (NotFoundException ex) {
            log.error("c=FindPartByIdUseCase m=execute s=not found error message={} partId={}", ex.getMessage(), identifier);
            throw new DomainException(ex.getMessage());
        } catch (Exception ex) {
            log.error("c=FindPartByIdUseCase m=execute s=generic error message={} partId={}", ex.getMessage(), identifier);
            throw new GenericException("genetic.error");
        }
    }
}
