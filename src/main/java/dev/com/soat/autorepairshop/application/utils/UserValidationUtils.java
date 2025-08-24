package dev.com.soat.autorepairshop.application.utils;

import dev.com.soat.autorepairshop.domain.entity.UserDomain;
import dev.com.soat.autorepairshop.domain.exception.template.ConflictException;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.gateway.UserGateway;
import dev.com.soat.autorepairshop.domain.objects.Document;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserValidationUtils {

    private final UserGateway userGateway;

    public UserDomain validateUserExistenceById(Long userId){
        UserDomain userDomain = userGateway.findByUserId(userId);
        if (userDomain == null) {
            log.error("c=UserValidationUtils m=validateUserExistenceById s=notFound userId = {}", userId);
            throw new NotFoundException("user.not.found");
        }

        return userDomain;
    }

    public UserDomain validateUserExistenceByEmail(String userEmail){
        UserDomain userDomain = userGateway.findByEmail(userEmail);
        if (userDomain == null) {
            log.error("c=UserValidationUtils m=validateUserExistenceByEmail s=notFound userEmail = {}", userEmail);
            throw new NotFoundException("user.not.found");
        }

        return userDomain;
    }

    public void validateNewUserByEmail(String userEmail) {
        UserDomain userAlreadyExists = userGateway.findByEmail(userEmail);

        if (userAlreadyExists != null){
            log.error("c=UserValidationUtils m=validateNewUserByEmail s=conflict userEmail = {}", userEmail);
            throw new ConflictException("user.already.exists");
        }
    }

    public void validateNewUserByDocument(String userDocument) {
        UserDomain userAlreadyExists = userGateway.findByDocument(userDocument);

        if (userAlreadyExists != null){
            Document document = Document.from(userDocument);
            log.error("c=UserValidationUtils m=validateNewUserByDocument s=conflict userDocument = {}", document.mask());
            throw new ConflictException("user.already.exists");
        }
    }
}
