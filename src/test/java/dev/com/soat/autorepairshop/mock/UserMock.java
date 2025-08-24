package dev.com.soat.autorepairshop.mock;

import dev.com.soat.autorepairshop.application.models.input.UserInputDTO;
import dev.com.soat.autorepairshop.application.models.output.UserOutputDTO;
import dev.com.soat.autorepairshop.domain.entity.UserDomain;
import dev.com.soat.autorepairshop.domain.enums.UserStatusType;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.UserRequestDTO;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.Objects;

public class UserMock {

    public static final Long VALID_IDENTIFIER = 1L;
    public static final String VALID_NAME = "John Doe";
    public static final String VALID_DOCUMENT = "52998224725";
    public static final String VALID_EMAIL = "john@email.com";
    public static final String VALID_PASSWORD = "password123";
    public static final LocalDateTime VALID_CONTRACTED_AT = LocalDateTime.now();
    public static final LocalDateTime VALID_CREATED_AT = LocalDateTime.now();
    public static final LocalDateTime VALID_UPDATED_AT = LocalDateTime.now();
    public static final String VALID_ROLE = "ADMIN";
    public static final String VALID_STATUS = "ACTIVE";
    public static final String VALID_INACTIVE_STATUS = "INACTIVE";

    public static UserDomain buildMockDomain(){
        return UserDomain.create(
                VALID_DOCUMENT,
                VALID_NAME,
                VALID_EMAIL,
                VALID_PASSWORD,
                VALID_CONTRACTED_AT,
                VALID_ROLE
        );
    }

    public static UserInputDTO buildMockInputDTO() {
        return new UserInputDTO(
                VALID_NAME,
                VALID_DOCUMENT,
                VALID_EMAIL,
                VALID_PASSWORD,
                VALID_CONTRACTED_AT,
                VALID_ROLE
        );
    }

    public static UserInputDTO buildMockInputDTO(
            final String name,
            final String document,
            final String email,
            final String password,
            final LocalDateTime contractedAt,
            final String role) {
        return new UserInputDTO(
                Objects.isNull(name) ? VALID_NAME : name,
                Objects.isNull(document) ? VALID_DOCUMENT : document,
                Objects.isNull(email) ? VALID_EMAIL : email,
                Objects.isNull(password) ? VALID_PASSWORD : password,
                Objects.isNull(contractedAt) ? VALID_CONTRACTED_AT : contractedAt,
                Objects.isNull(role) ? VALID_ROLE : role
        );
    }
    public static UserRequestDTO buildMockRequestDTO() {
        return new UserRequestDTO(
                VALID_NAME,
                VALID_DOCUMENT,
                VALID_EMAIL,
                VALID_PASSWORD,
                VALID_CONTRACTED_AT,
                VALID_ROLE
        );
    }

    public static UserRequestDTO buildMockRequestDTO(
            final String name,
            final String document,
            final String email,
            final String password,
            final LocalDateTime contractedAt,
            final String role) {
        return new UserRequestDTO(
                Objects.isNull(name) ? VALID_NAME : name,
                Objects.isNull(document) ? VALID_DOCUMENT : document,
                email,
                Objects.isNull(password) ? VALID_PASSWORD : password,
                Objects.isNull(contractedAt) ? VALID_CONTRACTED_AT : contractedAt,
                Objects.isNull(role) ? VALID_ROLE : role
        );
    }

    public static UserOutputDTO buildMockOutputDTO() {
        return new UserOutputDTO(
                VALID_IDENTIFIER,
                VALID_NAME,
                VALID_DOCUMENT,
                VALID_EMAIL,
                VALID_PASSWORD,
                VALID_CONTRACTED_AT,
                VALID_ROLE,
                VALID_STATUS,
                VALID_CREATED_AT,
                VALID_UPDATED_AT
        );
    }

    public static UserOutputDTO buildMockOutputInactiveDTO() {
        return new UserOutputDTO(
                VALID_IDENTIFIER,
                VALID_NAME,
                VALID_DOCUMENT,
                VALID_EMAIL,
                VALID_PASSWORD,
                VALID_CONTRACTED_AT,
                VALID_ROLE,
                VALID_INACTIVE_STATUS,
                VALID_CREATED_AT,
                VALID_UPDATED_AT
        );
    }

    public static UserOutputDTO buildMockOutputDTO(
            final Long identifier,
            final String name,
            final String document,
            final String email,
            final LocalDateTime contractedAt,
            final String role,
            final String status,
            final LocalDateTime createdAt,
            final LocalDateTime updatedAt
    ) {
        return new UserOutputDTO(
                Objects.isNull(identifier) ? VALID_IDENTIFIER : identifier,
                Objects.isNull(name) ? VALID_NAME : name,
                Objects.isNull(document) ? VALID_DOCUMENT : document,
                Objects.isNull(email) ? VALID_EMAIL : email,
                VALID_PASSWORD,
                Objects.isNull(contractedAt) ? VALID_CONTRACTED_AT : contractedAt,
                Objects.isNull(role) ? VALID_ROLE : role,
                Objects.isNull(status) ? VALID_STATUS : status,
                Objects.isNull(createdAt) ? VALID_CONTRACTED_AT : createdAt,
                Objects.isNull(updatedAt) ? VALID_UPDATED_AT : updatedAt
        );
    }

    public static UserEntity buildMockEntity(){
        return new UserEntity(
                VALID_IDENTIFIER,
                VALID_NAME,
                VALID_EMAIL,
                VALID_PASSWORD,
                VALID_DOCUMENT,
                UserStatusType.getTypeOrNull(VALID_STATUS),
                VALID_CONTRACTED_AT,
                RoleMock.buildMockEntity()
        );
    }

}
