package dev.com.soat.autorepairshop.application.usecase.user;

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
public class UpdateRoleUserUseCase {

    private final UserGateway userGateway;

    private final UserValidationUtils userValidationUtils;

    public void execute(final String employeeMail, final String newRole) {
        try {
            log.info("c=UpdateRoleUserUseCase m=execute s=start employeeMail={} newRole={}", employeeMail, newRole);
            if (newRole == null) {
                throw new DomainException("new.role.cannot.be.null");
            }
            final var emailObject = new Email(employeeMail);
            var user = userValidationUtils.validateUserExistenceByEmail(emailObject.getValue());
            user.updateRole(newRole);
            userGateway.save(user);
            log.info("c=UpdateRoleUserUseCase m=execute s=done employeeMail={} newRole={}", employeeMail, newRole);
        } catch (DomainException ex) {
            log.error("c=UpdateRoleUserUseCase m=execute s=domain error message={} employeeMail={}", ex.getMessage(), employeeMail);
            throw new DomainException(ex.getMessage());
        }  catch (NotFoundException ex) {
            log.error("c=UpdateRoleUserUseCase m=execute s=not found error message={} employeeMail={}", ex.getMessage(), employeeMail);
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            log.error("c=UpdateRoleUserUseCase m=execute s=generic error message={} employeeMail={}", ex.getMessage(), employeeMail);
            throw new GenericException("generic.error");
        }
    }
}
