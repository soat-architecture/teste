package dev.com.soat.autorepairshop.application.usecase.part;

import dev.com.soat.autorepairshop.domain.entity.PartDomain;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.exception.template.GenericException;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.exception.template.ValidatorException;
import dev.com.soat.autorepairshop.domain.gateway.PartGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@Slf4j
@RequiredArgsConstructor
public class DeletePartUseCase {

    private final PartGateway gateway;

    public void execute(Long identifier) {
        try {
            log.info("c=DeletePartUseCase m=execute s=start partId={}", identifier);
            final var part = gateway.findById(identifier);
            if (part == null) {
                throw new NotFoundException("part.not.found");
            }
            if (!part.getActive()) {
                throw new ValidatorException("part.already.inactive");
            }
            gateway.delete(identifier);
            log.info("c=DeletePartUseCase m=execute s=done partId={}", identifier);
        } catch (NotFoundException ex) {
            log.error("c=DeletePartUseCase m=execute s=not-found message={} partId={}", ex.getMessage(), identifier);
            throw new NotFoundException(ex.getMessage());
        } catch (ValidatorException ex) {
            log.error("c=DeletePartUseCase m=execute s=validation-error message={} partId={}", ex.getMessage(), identifier);
            throw new ValidatorException(ex.getMessage());
        } catch (DomainException ex) {
            log.error("c=DeletePartUseCase m=execute s=domain-error message={} partId={}", ex.getMessage(), identifier);
            throw new DomainException(ex.getMessage());
        } catch (Exception ex) {
            log.error("c=DeletePartUseCase m=execute s=generic-error message={} partId={}", ex.getMessage(), identifier);
            throw new GenericException("generic.error");
        }
    }
}
