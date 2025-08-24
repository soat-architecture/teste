package dev.com.soat.autorepairshop.application.usecase.user;

import dev.com.soat.autorepairshop.application.mapper.UserApplicationMapper;
import dev.com.soat.autorepairshop.application.models.output.UserOutputDTO;
import dev.com.soat.autorepairshop.application.utils.UserValidationUtils;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.exception.template.GenericException;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.gateway.UserGateway;
import dev.com.soat.autorepairshop.domain.objects.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FindUserByEmailUseCase {
    private final UserGateway userGateway;

    private final UserValidationUtils userValidationUtils;

    public UserOutputDTO execute(final String email) {
        try {
            log.info("c=FindUserByEmailUseCase m=execute s=start e-mail={}", email);
            final var emailObject = new Email(email);
            var user = userValidationUtils.validateUserExistenceByEmail(emailObject.getValue());
            log.info("c=FindUserByEmailUseCase m=execute s=done e-mail={}", email);
            return UserApplicationMapper.toDTO(user);
        } catch (DomainException ex) {
            log.error("c=FindUserByEmailUseCase m=execute s=domain error message={} e-mail={}", ex.getMessage(), email);
            throw new DomainException(ex.getMessage());
        }  catch (NotFoundException ex) {
            log.error("c=FindUserByEmailUseCase m=execute s=not found error message={} e-mail={}", ex.getMessage(), email);
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            log.error("c=FindUserByEmailUseCase m=execute s=generic error message={} e-mail={}", ex.getMessage(), email);
            throw new GenericException("generic.error");
        }
    }
}
