package dev.com.soat.autorepairshop.application.usecase.user;

import dev.com.soat.autorepairshop.application.mapper.UserApplicationMapper;
import dev.com.soat.autorepairshop.application.models.input.UserInputDTO;
import dev.com.soat.autorepairshop.application.utils.UserValidationUtils;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.exception.template.ConflictException;
import dev.com.soat.autorepairshop.domain.exception.template.GenericException;
import dev.com.soat.autorepairshop.domain.gateway.UserGateway;
import lombok.RequiredArgsConstructor;
import dev.com.soat.autorepairshop.domain.objects.Document;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CreateUserUseCase {

    private final UserGateway userGateway;

    private final UserValidationUtils userValidationUtils;

    public void execute(final UserInputDTO dto) {
        try {
            Document document = Document.from(dto.document());
            log.info("c=CreateUserUseCase m=execute s=start document={} e-mail={}", document.mask(), dto.email());
            userValidationUtils.validateNewUserByEmail(dto.email());
            userValidationUtils.validateNewUserByDocument(dto.document());
            final var domain = UserApplicationMapper.toDomain(dto);
            userGateway.save(domain);
            log.info("c=CreateUserUseCase m=execute s=done e-mail={}", dto.email());
        } catch (DomainException ex) {
            log.error("c=CreateUserUseCase m=execute s=domain error message={} e-mail={}", ex.getMessage(), dto.email());
            throw new DomainException(ex.getMessage());
        } catch (ConflictException ex) {
            log.error("c=CreateUserUseCase m=execute s=conflict error message={} e-mail={}", ex.getMessage(), dto.email());
            throw new ConflictException(ex.getMessage());
        } catch (Exception ex) {
            log.error("c=CreateUserUseCase m=execute s=generic error message={} e-mail={}", ex.getMessage(), dto.email());
            throw new GenericException("generic.error");
        }
    }
}
