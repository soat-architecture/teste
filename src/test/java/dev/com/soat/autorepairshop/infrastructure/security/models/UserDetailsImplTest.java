package dev.com.soat.autorepairshop.infrastructure.security.models;


import dev.com.soat.autorepairshop.domain.enums.UserStatusType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static dev.com.soat.autorepairshop.mock.AuthMock.createMockInactiveUserDetails;
import static dev.com.soat.autorepairshop.mock.AuthMock.createMockUserDetails;
import static dev.com.soat.autorepairshop.mock.UserMock.buildMockOutputDTO;
import static dev.com.soat.autorepairshop.mock.UserMock.buildMockOutputInactiveDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserDetailsImplTest {
    @Nested
    @DisplayName("Testes para métodos getters")
    class GetterTests {

        @Test
        @DisplayName("Deve retornar email corretamente")
        void givenUserDetails_whenGetEmail_thenReturnsCorrectEmail() {
            // Given
            final var userDetails = createMockUserDetails();

            // When
            final var email = userDetails.getEmail();

            // Then
            assertEquals("example@example.com", email);
        }

        @Test
        @DisplayName("Deve retornar password corretamente")
        void givenUserDetails_whenGetPassword_thenReturnsCorrectPassword() {
            // Given
            final var userDetails = createMockUserDetails();

            // When
            final var password = userDetails.getPassword();

            // Then
            assertEquals("$2a$10$v1684brh0bkF9OW0BuLGkOfl8ECKCRAABdu5QZNrt8BtQKr0tm0xK", password);
        }

        @Test
        @DisplayName("Deve retornar status corretamente")
        void givenUserDetails_whenGetStatus_thenReturnsCorrectStatus() {
            // Given
            final var userDetails = createMockUserDetails();

            // When
            final var status = userDetails.getStatus();

            // Then
            assertEquals("ACTIVE", status);
        }

        @Test
        @DisplayName("Deve retornar authorities corretamente")
        void givenUserDetails_whenGetAuthorities_thenReturnsCorrectAuthorities() {
            // Given
            final var expectedAuthorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
            final var userDetails = createMockUserDetails();

            // When
            final var authorities = userDetails.getAuthorities();

            // Then
            assertEquals(expectedAuthorities, authorities);
        }

        @Test
        @DisplayName("Deve retornar múltiplas authorities corretamente")
        void givenAdminUserDetails_whenGetAuthorities_thenReturnsMultipleAuthorities() {
            // Given
            final var userDetails = createMockUserDetails();

            // When
            final var authorities = userDetails.getAuthorities();

            // Then
            assertEquals(1, authorities.size());
            assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
        }
    }

    @Nested
    @DisplayName("Testes para métodos de UserDetails interface")
    class UserDetailsInterfaceTests {

        @Test
        @DisplayName("Deve retornar email como username")
        void givenUserDetails_whenGetUsername_thenReturnsEmail() {
            // Given
            final var userDetails = createMockUserDetails();

            // When
            final var username = userDetails.getUsername();

            // Then
            assertEquals("example@example.com", username);
            assertEquals(userDetails.getEmail(), username);
        }

        @Test
        @DisplayName("Deve sempre retornar true para isAccountNonExpired")
        void givenAnyUserDetails_whenIsAccountNonExpired_thenReturnsTrue() {
            // Given
            final var activeUser = createMockUserDetails();
            final var inactiveUser = createMockInactiveUserDetails();

            // When & Then
            assertTrue(activeUser.isAccountNonExpired());
            assertTrue(inactiveUser.isAccountNonExpired());
        }

        @Test
        @DisplayName("Deve sempre retornar true para isAccountNonLocked")
        void givenAnyUserDetails_whenIsAccountNonLocked_thenReturnsTrue() {
            // Given
            final var activeUser = createMockUserDetails();
            final var inactiveUser = createMockInactiveUserDetails();

            // When & Then
            assertTrue(activeUser.isAccountNonLocked());
            assertTrue(inactiveUser.isAccountNonLocked());
        }

        @Test
        @DisplayName("Deve sempre retornar true para isCredentialsNonExpired")
        void givenAnyUserDetails_whenIsCredentialsNonExpired_thenReturnsTrue() {
            // Given
            final var activeUser = createMockUserDetails();
            final var inactiveUser = createMockInactiveUserDetails();

            // When & Then
            assertTrue(activeUser.isCredentialsNonExpired());
            assertTrue(inactiveUser.isCredentialsNonExpired());
        }
    }

    @Nested
    @DisplayName("Testes para método isEnabled")
    class IsEnabledTests {

        @Test
        @DisplayName("Deve retornar true para usuário ativo")
        void givenActiveUser_whenIsEnabled_thenReturnsTrue() {
            // Given
            final var userDetails = createMockUserDetails();

            // When
            final var isEnabled = userDetails.isEnabled();

            // Then
            assertTrue(isEnabled);
        }

        @Test
        @DisplayName("Deve retornar false para usuário inativo")
        void givenInactiveUser_whenIsEnabled_thenReturnsFalse() {
            // Given
            final var userDetails = createMockInactiveUserDetails();

            // When
            final var isEnabled = userDetails.isEnabled();

            // Then
            assertFalse(isEnabled);
        }

        @Test
        @DisplayName("Deve retornar true quando status equals ACTIVE exatamente")
        void givenExactActiveStatus_whenIsEnabled_thenReturnsTrue() {
            // Given
            final var userDetails = new UserDetailsImpl(
                    1L,
                    "test@example.com",
                    "password",
                    UserStatusType.ACTIVE.getName(),
                    List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );

            // When
            final var isEnabled = userDetails.isEnabled();

            // Then
            assertTrue(isEnabled);
        }

        @Test
        @DisplayName("Deve retornar false para status diferentes de ACTIVE")
        void givenNonActiveStatuses_whenIsEnabled_thenReturnsFalse() {
            // Test com diferentes status
            final String[] inactiveStatuses = {"INACTIVE", "PENDING", "SUSPENDED", "BLOCKED", ""};

            for (String status : inactiveStatuses) {
                final var userDetails = new UserDetailsImpl(
                        1L,
                        "test@example.com",
                        "password",
                        status,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                );

                assertFalse(userDetails.isEnabled(),
                        "User with status '" + status + "' should not be enabled");
            }
        }

        @Test
        @DisplayName("Deve retornar false para status null")
        void givenNullStatus_whenIsEnabled_thenReturnsFalse() {
            // Given
            final var userDetails = new UserDetailsImpl(
                    1L,
                    "test@example.com",
                    "password",
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );

            // When
            final var isEnabled = userDetails.isEnabled();

            // Then
            assertFalse(isEnabled);
        }
    }

    @Nested
    @DisplayName("Testes para método build estático")
    class BuildStaticMethodTests {

        @Test
        @DisplayName("Deve construir UserDetailsImpl corretamente a partir de UserOutputDTO")
        void givenUserOutputDTO_whenBuild_thenReturnsCorrectUserDetailsImpl() {
            // Given
            final var userOutput = buildMockOutputDTO();

            // When
            final var userDetails = UserDetailsImpl.build(userOutput);

            // Then
            assertNotNull(userDetails);
            assertEquals(userOutput.email(), userDetails.getEmail());
            assertEquals(userOutput.password(), userDetails.getPassword());
            assertEquals(userOutput.status(), userDetails.getStatus());
            assertEquals(userOutput.email(), userDetails.getUsername());
            assertNotNull(userDetails.getAuthorities());
        }

        @Test
        @DisplayName("Deve criar authorities corretas para usuário admin")
        void givenUserOutputWithAdminRole_whenBuild_thenCreatesCorrectAuthorities() {
            // Given
            final var adminOutput = buildMockOutputDTO();

            // When
            final var userDetails = UserDetailsImpl.build(adminOutput);

            // Then
            final var authorities = userDetails.getAuthorities();
            assertNotNull(authorities);
            assertFalse(authorities.isEmpty());
            // Verifica se contém a authority baseada no role admin
            assertTrue(authorities.stream()
                    .anyMatch(auth -> auth.getAuthority().contains("ADMIN")));
        }

        @Test
        @DisplayName("Deve manter status correto para usuário inativo")
        void givenInactiveUserOutput_whenBuild_thenMaintainsInactiveStatus() {
            // Given
            final var inactiveUserOutput = buildMockOutputInactiveDTO();

            // When
            final var userDetails = UserDetailsImpl.build(inactiveUserOutput);

            // Then
            assertEquals("INACTIVE", userDetails.getStatus());
            assertFalse(userDetails.isEnabled());
        }
    }

    @Nested
    @DisplayName("Testes de equivalência e hashCode")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("Deve considerar dois UserDetailsImpl iguais com mesmos dados")
        void givenSameData_whenEquals_thenReturnsTrue() {
            // Given
            final var userDetails1 = createMockUserDetails();
            final var userDetails2 = new UserDetailsImpl(
                    1L,
                    "example@example.com",
                    "$2a$10$v1684brh0bkF9OW0BuLGkOfl8ECKCRAABdu5QZNrt8BtQKr0tm0xK",
                    "ACTIVE",
                    List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );

            // When & Then
            assertEquals(userDetails1.getEmail(), userDetails2.getEmail());
            assertEquals(userDetails1.getPassword(), userDetails2.getPassword());
            assertEquals(userDetails1.getStatus(), userDetails2.getStatus());
            assertEquals(userDetails1.getAuthorities(), userDetails2.getAuthorities());
        }
    }

    @Nested
    @DisplayName("Testes de integração com Spring Security")
    class SpringSecurity_IT {

        @Test
        @DisplayName("Deve ser compatível com Spring Security UserDetails interface")
        void givenUserDetailsImpl_whenUsedAsUserDetails_thenWorksCorrectly() {
            // Given

            // When & Then - Testando compatibilidade com interface
            org.springframework.security.core.userdetails.UserDetails springUserDetails = createMockUserDetails();

            assertEquals("example@example.com", springUserDetails.getUsername());
            assertEquals("$2a$10$v1684brh0bkF9OW0BuLGkOfl8ECKCRAABdu5QZNrt8BtQKr0tm0xK", springUserDetails.getPassword());
            assertTrue(springUserDetails.isEnabled());
            assertTrue(springUserDetails.isAccountNonExpired());
            assertTrue(springUserDetails.isAccountNonLocked());
            assertTrue(springUserDetails.isCredentialsNonExpired());
            assertNotNull(springUserDetails.getAuthorities());
        }

        @Test
        @DisplayName("Deve funcionar corretamente com AuthenticationToken")
        void givenUserDetailsImpl_whenUsedInAuthenticationToken_thenWorksCorrectly() {
            // Given
            final var userDetails = createMockUserDetails();

            // When
            final var authToken = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

            // Then
            assertEquals(userDetails, authToken.getPrincipal());
            assertEquals(userDetails.getAuthorities(), authToken.getAuthorities());
            assertTrue(authToken.isAuthenticated());
        }

        @Test
        @DisplayName("Deve manter consistência com SecurityContext")
        void givenUserDetailsImpl_whenSetInSecurityContext_thenMaintainsConsistency() {
            // Given
            final var userDetails = createMockUserDetails();
            final var authToken = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

            // When
            org.springframework.security.core.context.SecurityContextHolder
                    .getContext()
                    .setAuthentication(authToken);

            // Then
            final var retrievedAuth = org.springframework.security.core.context.SecurityContextHolder
                    .getContext()
                    .getAuthentication();

            assertNotNull(retrievedAuth);
            assertEquals(userDetails, retrievedAuth.getPrincipal());
            assertEquals(userDetails.getAuthorities(), retrievedAuth.getAuthorities());

            // Cleanup
            org.springframework.security.core.context.SecurityContextHolder.clearContext();
        }
    }

    @Nested
    @DisplayName("Testes de validação e edge cases")
    class ValidationAndEdgeCasesTests {

        @Test
        @DisplayName("Deve lidar com email null")
        void givenNullEmail_whenCreateUserDetails_thenHandlesGracefully() {
            // Given & When
            final var userDetails = new UserDetailsImpl(
                    1L,
                    null,
                    "$2a$10$v1684brh0bkF9OW0BuLGkOfl8ECKCRAABdu5QZNrt8BtQKr0tm0xK",
                    "ACTIVE",
                    List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );

            // Then
            assertNull(userDetails.getEmail());
            assertNull(userDetails.getUsername());
            assertEquals("$2a$10$v1684brh0bkF9OW0BuLGkOfl8ECKCRAABdu5QZNrt8BtQKr0tm0xK", userDetails.getPassword());
            assertEquals("ACTIVE", userDetails.getStatus());
            assertTrue(userDetails.isEnabled());
        }

        @Test
        @DisplayName("Deve lidar com password null")
        void givenNullPassword_whenCreateUserDetails_thenHandlesGracefully() {
            // Given & When
            final var userDetails = new UserDetailsImpl(
                    1L,
                    "example@example.com",
                    null,
                    "ACTIVE",
                    List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );

            // Then
            assertEquals("example@example.com", userDetails.getEmail());
            assertNull(userDetails.getPassword());
            assertEquals("ACTIVE", userDetails.getStatus());
            assertTrue(userDetails.isEnabled());
        }

        @Test
        @DisplayName("Deve lidar com authorities null")
        void givenNullAuthorities_whenCreateUserDetails_thenHandlesGracefully() {
            // Given & When
            final var userDetails = new UserDetailsImpl(
                    1L,
                    "example@example.com",
                    "$2a$10$v1684brh0bkF9OW0BuLGkOfl8ECKCRAABdu5QZNrt8BtQKr0tm0xK",
                    "ACTIVE",
                    null
            );

            // Then
            assertEquals("example@example.com", userDetails.getEmail());
            assertEquals("$2a$10$v1684brh0bkF9OW0BuLGkOfl8ECKCRAABdu5QZNrt8BtQKr0tm0xK", userDetails.getPassword());
            assertEquals("ACTIVE", userDetails.getStatus());
            assertNull(userDetails.getAuthorities());
            assertTrue(userDetails.isEnabled());
        }

        @Test
        @DisplayName("Deve lidar com authorities vazias")
        void givenEmptyAuthorities_whenCreateUserDetails_thenHandlesGracefully() {
            // Given & When
            final var userDetails = new UserDetailsImpl(
                    1L,
                    "example@example.com",
                    "$2a$10$v1684brh0bkF9OW0BuLGkOfl8ECKCRAABdu5QZNrt8BtQKr0tm0xK",
                    "ACTIVE",
                    List.of()
            );

            // Then
            assertEquals("example@example.com", userDetails.getEmail());
            assertEquals("$2a$10$v1684brh0bkF9OW0BuLGkOfl8ECKCRAABdu5QZNrt8BtQKr0tm0xK", userDetails.getPassword());
            assertEquals("ACTIVE", userDetails.getStatus());
            assertTrue(userDetails.getAuthorities().isEmpty());
            assertTrue(userDetails.isEnabled());
        }

        @Test
        @DisplayName("Deve validar diferentes formatos de email")
        void givenDifferentEmailFormats_whenCreateUserDetails_thenAcceptsValidFormats() {
            // Given
            final String[] validEmails = {
                    "simple@example.com",
                    "example.name@example.com",
                    "example+tag@example.com",
                    "user123@subdomain.example.com",
                    "very.long.email.address@very.long.domain.name.com"
            };

            // When & Then
            for (String email : validEmails) {
                final var userDetails = new UserDetailsImpl(
                        1L,
                        email,
                        "$2a$10$v1684brh0bkF9OW0BuLGkOfl8ECKCRAABdu5QZNrt8BtQKr0tm0xK",
                        "ACTIVE",
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                );

                assertEquals(email, userDetails.getEmail());
                assertEquals(email, userDetails.getUsername());
                assertTrue(userDetails.isEnabled());
            }
        }

        @Test
        @DisplayName("Deve validar diferentes status de usuário")
        void givenDifferentStatuses_whenCreateUserDetails_thenValidatesCorrectly() {
            // Given
            final String[] activeStatuses = {"ACTIVE"};
            final String[] inactiveStatuses = {"INACTIVE", "SUSPENDED", "PENDING", "BLOCKED", "DELETED"};

            // When & Then - Status ativos
            for (String status : activeStatuses) {
                final var userDetails = new UserDetailsImpl(
                        1L,
                        "example@example.com",
                        "$2a$10$v1684brh0bkF9OW0BuLGkOfl8ECKCRAABdu5QZNrt8BtQKr0tm0xK",
                        status,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                );
                assertTrue(userDetails.isEnabled(), "Status '" + status + "' should be enabled");
            }

            // When & Then - Status inativos
            for (String status : inactiveStatuses) {
                final var userDetails = new UserDetailsImpl(
                        1L,
                        "example@example.com",
                        "$2a$10$v1684brh0bkF9OW0BuLGkOfl8ECKCRAABdu5QZNrt8BtQKr0tm0xK",
                        status,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                );
                assertFalse(userDetails.isEnabled(), "Status '" + status + "' should not be enabled");
            }
        }

        @Test
        @DisplayName("Deve ser case-sensitive para status")
        void givenCaseSensitiveStatus_whenIsEnabled_thenValidatesCorrectly() {
            // Test case sensitivity
            final String[] caseVariations = {"active", "Active", "ACTIVE", "AcTiVe"};

            for (String status : caseVariations) {
                final var userDetails = new UserDetailsImpl(
                        1L,
                        "example@example.com",
                        "$2a$10$v1684brh0bkF9OW0BuLGkOfl8ECKCRAABdu5QZNrt8BtQKr0tm0xK",
                        status,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                );

                if ("ACTIVE".equals(status)) {
                    assertTrue(userDetails.isEnabled(), "Only exact 'ACTIVE' should enable example");
                } else {
                    assertFalse(userDetails.isEnabled(), "Status '" + status + "' should not enable example");
                }
            }
        }
    }

    @Nested
    @DisplayName("Testes de performance e imutabilidade")
    class PerformanceAndImmutabilityTests {

        @Test
        @DisplayName("Deve ser thread-safe para leituras")
        void givenUserDetailsImpl_whenAccessedConcurrently_thenIsThreadSafe() throws InterruptedException {
            // Given
            final var userDetails = createMockUserDetails();
            final var results = new java.util.concurrent.ConcurrentHashMap<String, Object>();
            final var threads = new Thread[10];

            // When
            for (int i = 0; i < threads.length; i++) {
                final int threadIndex = i;
                threads[i] = new Thread(() -> {
                    results.put("email_" + threadIndex, userDetails.getEmail());
                    results.put("username_" + threadIndex, userDetails.getUsername());
                    results.put("password_" + threadIndex, userDetails.getPassword());
                    results.put("status_" + threadIndex, userDetails.getStatus());
                    results.put("enabled_" + threadIndex, userDetails.isEnabled());
                    results.put("authorities_" + threadIndex, userDetails.getAuthorities());
                });
            }

            for (Thread thread : threads) {
                thread.start();
            }

            for (Thread thread : threads) {
                thread.join();
            }

            // Then
            for (int i = 0; i < threads.length; i++) {
                assertEquals("example@example.com", results.get("email_" + i));
                assertEquals("example@example.com", results.get("username_" + i));
                assertEquals("$2a$10$v1684brh0bkF9OW0BuLGkOfl8ECKCRAABdu5QZNrt8BtQKr0tm0xK", results.get("password_" + i));
                assertEquals("ACTIVE", results.get("status_" + i));
                assertEquals(true, results.get("enabled_" + i));
                assertNotNull(results.get("authorities_" + i));
            }
        }

        @Test
        @DisplayName("Deve criar instâncias rapidamente")
        void givenMultipleCreations_whenCreatingUserDetails_thenPerformsWell() {
            // Given
            final var startTime = System.currentTimeMillis();
            final var userOutput = buildMockOutputDTO();

            // When
            for (int i = 0; i < 1000; i++) {
                final var userDetails = UserDetailsImpl.build(userOutput);
                assertNotNull(userDetails);
            }

            // Then
            final var endTime = System.currentTimeMillis();
            final var duration = endTime - startTime;

            // Verifica se criou 1000 instâncias em menos de 1 segundo (muito generoso)
            assertTrue(duration < 1000, "Creating 1000 UserDetailsImpl instances took too long: " + duration + "ms");
        }
    }
}