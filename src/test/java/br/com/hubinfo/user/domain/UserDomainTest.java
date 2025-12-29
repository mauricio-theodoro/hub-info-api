package br.com.hubinfo.user.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários do domínio.
 *
 * Objetivo:
 * - Validar regras centrais sem depender de Spring, banco, web, etc.
 * - Isso é um dos pilares da Clean Architecture: domínio testável isoladamente.
 */
class UserDomainTest {

    @Test
    void shouldCreateUserWhenAdult() {
        LocalDate today = LocalDate.of(2025, 12, 29);
        LocalDate birthDate = LocalDate.of(2000, 1, 1);

        User user = User.createNew(
                "Mauricio",
                "Theodoro",
                EmailAddress.of("mauricio@exemplo.com"),
                "hashed-password",
                birthDate,
                Set.of(Role.USER),
                today,
                Instant.parse("2025-12-29T15:00:00Z")
        );

        assertNotNull(user.id());
        assertEquals("Mauricio", user.firstName());
        assertEquals("Theodoro", user.lastName());
        assertEquals("mauricio@exemplo.com", user.email().value());
        assertTrue(user.roles().contains(Role.USER));
    }

    @Test
    void shouldRejectWhenUnder18() {
        LocalDate today = LocalDate.of(2025, 12, 29);
        LocalDate birthDate = LocalDate.of(2010, 1, 1);

        assertThrows(UnderageUserException.class, () ->
                User.createNew(
                        "Joao",
                        "Silva",
                        EmailAddress.of("joao@exemplo.com"),
                        "hashed-password",
                        birthDate,
                        Set.of(Role.USER),
                        today,
                        Instant.parse("2025-12-29T15:00:00Z")
                )
        );
    }

    @Test
    void shouldRejectInvalidEmail() {
        assertThrows(InvalidEmailException.class, () -> EmailAddress.of("email-invalido"));
    }
}
