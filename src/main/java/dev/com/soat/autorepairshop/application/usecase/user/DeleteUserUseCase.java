package dev.com.soat.autorepairshop.application.usecase.user;

import dev.com.soat.autorepairshop.application.utils.UserValidationUtils;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.exception.template.GenericException;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.gateway.UserGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteUserUseCase {
    private final UserGateway userGateway;

    private final UserValidationUtils userValidationUtils;

    public void execute(final Long userId) {
        try {
            log.info("c=DeleteUserUseCase m=execute s=start userId={}", userId);
            var user = userValidationUtils.validateUserExistenceById(userId);
            user.delete();
            userGateway.save(user);
            log.info("c=DeleteUserUseCase m=execute s=done userId={}", userId);
        } catch (DomainException ex) {
            log.error("c=DeleteUserUseCase m=execute s=domain error userId={} message={}", userId, ex.getMessage());
            throw new DomainException(ex.getMessage());
        }  catch (NotFoundException ex) {
            log.error("c=DeleteUserUseCase m=execute s=not found error userId={} message={}", userId, ex.getMessage());
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            log.error("c=UpdateRoleUserUseCase m=execute s=generic error userId={} message={}", userId, ex.getMessage());
            throw new GenericException("generic.error");
        }
    }
}
