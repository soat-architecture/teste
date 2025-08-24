package dev.com.soat.autorepairshop.domain.objects;

import dev.com.soat.autorepairshop.domain.enums.BasicRoleType;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @ParameterizedTest
    @EnumSource(BasicRoleType.class)
    @DisplayName("Should create Role when value is a valid BasicRole")
    void shouldCreateRoleWhenValueIsValidBasicRole(BasicRoleType validRole) {
        // When creating a role with a valid value
        Role role = new Role(validRole.name());

        // Then
        assertNotNull(role);
        assertEquals(validRole.name(), role.getValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "INVALID_ROLE",
            "User",
            "admin",
            "MECHANIC", // similar but not exact
            "ADMINISTRATOR "  // with space
    })
    @DisplayName("Should throw DomainException when value is not a valid BasicRole")
    void shouldThrowExceptionWhenValueIsNotValidBasicRole(String invalidRole) {
        // When creating a role with invalid value and Then should throw
        DomainException exception = assertThrows(
                DomainException.class,
                () -> new Role(invalidRole)
        );

        assertEquals("generic.error", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should throw DomainException when value is null or empty")
    void shouldThrowExceptionWhenValueIsNullOrEmpty(String invalidValue) {
        // When creating a role with null/empty value and Then should throw
        assertThrows(
                DomainException.class,
                () -> new Role(invalidValue)
        );
    }

    @Test
    @DisplayName("Should return correct value when getValue is called")
    void shouldReturnCorrectValueWhenGetValueIsCalled() {
        // Given a valid role
        String expectedValue = BasicRoleType.ADMIN.name();
        Role role = new Role(expectedValue);

        // When getting value
        String actualValue = role.getValue();

        // Then
        assertEquals(expectedValue, actualValue);
    }
}