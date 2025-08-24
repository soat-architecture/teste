package dev.com.soat.autorepairshop.domain.entity;

import dev.com.soat.autorepairshop.domain.enums.UserStatusType;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.mock.UserMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserDomainTest {


    @Nested
    @DisplayName("Create User Tests")
    class CreateUserTests {
        @Test
        @DisplayName("Should create user successfully with valid data")
        void shouldCreateUserSuccessfully() {
            // When
            final var user = UserMock.buildMockDomain();

            // Then
            assertNotNull(user);
            assertNull(user.getIdentifier());
            assertEquals(UserMock.VALID_NAME, user.getName());
            assertEquals(UserMock.VALID_DOCUMENT, user.getDocument());
            assertEquals(UserMock.VALID_EMAIL, user.getEmail());
            assertEquals(UserMock.VALID_PASSWORD, user.getPassword());
            assertEquals(UserMock.VALID_CONTRACTED_AT, user.getContractedAt());
            assertEquals(UserMock.VALID_ROLE, user.getRole());
            assertEquals(UserMock.VALID_STATUS, user.getStatus());
            assertNotNull(user.getCreatedAt());
            assertNull(user.getUpdatedAt());
        }

        @ParameterizedTest
        @DisplayName("Should throw exception when document is invalid")
        @ValueSource(strings = {
                "11111111111",
                "22222222222",
                "12345",
                "123456789012"
        })
        void shouldThrowExceptionWhenDocumentIsInvalid(String invalidDocument) {
            // When/Then
            DomainException exception = assertThrows(
                    DomainException.class,
                    () -> UserDomain.create(
                            invalidDocument,
                            UserMock.VALID_NAME,
                            UserMock.VALID_EMAIL,
                            UserMock.VALID_PASSWORD,
                            UserMock.VALID_CONTRACTED_AT,
                            UserMock.VALID_ROLE
                    )
            );

            assertEquals("document.is.not.valid", exception.getMessage());
        }

        @ParameterizedTest
        @DisplayName("Should throw exception when email is invalid")
        @ValueSource(strings = {
                "invalid-email",
                "@domain.com",
                "user@",
                "user@domain"
        })
        void shouldThrowExceptionWhenEmailIsInvalid(String invalidEmail) {
            // When/Then
            DomainException exception = assertThrows(
                    DomainException.class,
                    () -> UserDomain.create(
                            UserMock.VALID_DOCUMENT,
                            UserMock.VALID_NAME,
                            invalidEmail,
                            UserMock.VALID_PASSWORD,
                            UserMock.VALID_CONTRACTED_AT,
                            UserMock.VALID_ROLE
                    )
            );

            assertEquals("email.is.not.valid", exception.getMessage());
        }

        @ParameterizedTest
        @DisplayName("Should throw exception when role is invalid")
        @ValueSource(strings = {
                "",
                "INVALID_ROLE",
                "USER_ADMIN"
        })
        void shouldThrowExceptionWhenRoleIsInvalid(String invalidRole) {
            // When/Then
            DomainException exception = assertThrows(
                    DomainException.class,
                    () -> UserDomain.create(
                            UserMock.VALID_DOCUMENT,
                            UserMock.VALID_NAME,
                            UserMock.VALID_EMAIL,
                            UserMock.VALID_PASSWORD,
                            UserMock.VALID_CONTRACTED_AT,
                            invalidRole
                    )
            );

            assertEquals("generic.error", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Restore User Tests")
    class RestoreUserTests {
        @Test
        @DisplayName("Should restore user successfully with valid data")
        void shouldRestoreUserSuccessfully() {
            // Given
            Long identifier = 1L;
            LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
            LocalDateTime updatedAt = LocalDateTime.now();

            // When
            UserDomain user = UserDomain.restore(
                    identifier,
                    UserMock.VALID_NAME,
                    UserMock.VALID_DOCUMENT,
                    UserMock.VALID_EMAIL,
                    UserMock.VALID_PASSWORD,
                    UserMock.VALID_CONTRACTED_AT,
                    UserMock.VALID_ROLE,
                    UserMock.VALID_STATUS,
                    createdAt,
                    updatedAt
            );

            // Then
            assertNotNull(user);
            assertEquals(identifier, user.getIdentifier());
            assertEquals(UserMock.VALID_NAME, user.getName());
            assertEquals(UserMock.VALID_DOCUMENT, user.getDocument());
            assertEquals(UserMock.VALID_EMAIL, user.getEmail());
            assertEquals(UserMock.VALID_PASSWORD, user.getPassword());
            assertEquals(UserMock.VALID_CONTRACTED_AT, user.getContractedAt());
            assertEquals(UserMock.VALID_ROLE, user.getRole());
            assertEquals(createdAt, user.getCreatedAt());
            assertEquals(updatedAt, user.getUpdatedAt());
        }

        @Test
        @DisplayName("Should throw exception when restoring with invalid document")
        void shouldThrowExceptionWhenRestoringWithInvalidDocument() {
            // When/Then
            DomainException exception = assertThrows(
                    DomainException.class,
                    () -> UserDomain.restore(
                            1L,
                            UserMock.VALID_NAME,
                            "11111111111",
                            UserMock.VALID_EMAIL,
                            UserMock.VALID_PASSWORD,
                            UserMock.VALID_CONTRACTED_AT,
                            UserMock.VALID_ROLE,
                            UserMock.VALID_STATUS,
                            LocalDateTime.now(),
                            LocalDateTime.now()
                    )
            );

            assertEquals("document.is.not.valid", exception.getMessage());
        }

        @Test
        @DisplayName("Should delete user successfully")
        void shouldDeleteUserSuccessfully() {
            // Given
            UserDomain user = UserDomain.create(
                    UserMock.VALID_DOCUMENT,
                    UserMock.VALID_NAME,
                    UserMock.VALID_EMAIL,
                    UserMock.VALID_PASSWORD,
                    UserMock.VALID_CONTRACTED_AT,
                    UserMock.VALID_ROLE
            );

            // When
            user.delete();

            // Then
            assertEquals("DELETED", user.getStatus());
            assertEquals(UserStatusType.DELETED, user.getStatusType());
            assertNotNull(user.getUpdatedAt());
        }

        @Test
        @DisplayName("Should update role successfully with valid role")
        void shouldUpdateRoleSuccessfully() {
            // Given
            UserDomain user = UserDomain.create(
                    UserMock.VALID_DOCUMENT,
                    UserMock.VALID_NAME,
                    UserMock.VALID_EMAIL,
                    UserMock.VALID_PASSWORD,
                    UserMock.VALID_CONTRACTED_AT,
                    UserMock.VALID_ROLE
            );
            String newRole = "MECHANICAL";

            // When
            user.updateRole(newRole);

            // Then
            assertEquals(newRole, user.getRole());
            assertNotNull(user.getUpdatedAt());
        }

        @Test
        @DisplayName("Should throw exception when updating with invalid role")
        void shouldThrowExceptionWhenUpdatingWithInvalidRole() {
            // Given
            UserDomain user = UserDomain.create(
                    UserMock.VALID_DOCUMENT,
                    UserMock.VALID_NAME,
                    UserMock.VALID_EMAIL,
                    UserMock.VALID_PASSWORD,
                    UserMock.VALID_CONTRACTED_AT,
                    UserMock.VALID_ROLE
            );
            String invalidRole = "INVALID_ROLE";

            // When/Then
            DomainException exception = assertThrows(
                    DomainException.class,
                    () -> user.updateRole(invalidRole)
            );

            assertEquals("generic.error", exception.getMessage());
        }

        @Test
        @DisplayName("Should return correct status type when status is valid")
        void shouldReturnCorrectStatusType() {
            // Given
            UserDomain user = UserDomain.create(
                    UserMock.VALID_DOCUMENT,
                    UserMock.VALID_NAME,
                    UserMock.VALID_EMAIL,
                    UserMock.VALID_PASSWORD,
                    UserMock.VALID_CONTRACTED_AT,
                    UserMock.VALID_ROLE
            );

            // When
            UserStatusType statusType = user.getStatusType();

            // Then
            assertEquals(UserStatusType.ACTIVE, statusType);
        }
    }
}