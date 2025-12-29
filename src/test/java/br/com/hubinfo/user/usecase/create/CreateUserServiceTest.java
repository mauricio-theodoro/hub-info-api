package br.com.hubinfo.user.usecase.create;

import br.com.hubinfo.common.audit.AuditEvent;
import br.com.hubinfo.common.time.TimeProvider;
import br.com.hubinfo.user.domain.EmailAddress;
import br.com.hubinfo.user.domain.User;
import br.com.hubinfo.user.usecase.port.AuditPort;
import br.com.hubinfo.user.usecase.port.PasswordHasherPort;
import br.com.hubinfo.user.usecase.port.UserRepositoryPort;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CreateUserServiceTest {

    @Test
    void shouldCreateUserWhenValid() {
        FakeUserRepository repo = new FakeUserRepository();
        FakePasswordHasher hasher = new FakePasswordHasher();
        FakeAudit audit = new FakeAudit();
        FixedTimeProvider time = new FixedTimeProvider(
                LocalDate.of(2025, 12, 29),
                Instant.parse("2025-12-29T15:00:00Z")
        );

        CreateUserService service = new CreateUserService(repo, hasher, audit, time);

        CreateUserCommand cmd = new CreateUserCommand(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "Mauricio",
                "Theodoro",
                "mauricio@exemplo.com",
                "SenhaForte123",
                "SenhaForte123",
                LocalDate.of(2000, 1, 1)
        );

        CreateUserResult result = service.execute(cmd);

        assertNotNull(result.userId());
        assertEquals("mauricio@exemplo.com", result.email());
        assertTrue(repo.existsByEmail(EmailAddress.of("mauricio@exemplo.com")));
        assertFalse(audit.events.isEmpty());
    }

    @Test
    void shouldRejectWhenEmailAlreadyExists() {
        FakeUserRepository repo = new FakeUserRepository();
        FakePasswordHasher hasher = new FakePasswordHasher();
        FakeAudit audit = new FakeAudit();
        FixedTimeProvider time = new FixedTimeProvider(
                LocalDate.of(2025, 12, 29),
                Instant.parse("2025-12-29T15:00:00Z")
        );

        // Seed: simula usuário já existente
        repo.seedEmail(EmailAddress.of("duplicado@exemplo.com"));

        CreateUserService service = new CreateUserService(repo, hasher, audit, time);

        CreateUserCommand cmd = new CreateUserCommand(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "Maria",
                "Silva",
                "duplicado@exemplo.com",
                "SenhaForte123",
                "SenhaForte123",
                LocalDate.of(1990, 1, 1)
        );

        assertThrows(UserUseCaseException.class, () -> service.execute(cmd));
        assertTrue(audit.events.stream().anyMatch(e -> !e.success()));
    }

    @Test
    void shouldRejectWhenPasswordConfirmationMismatch() {
        FakeUserRepository repo = new FakeUserRepository();
        FakePasswordHasher hasher = new FakePasswordHasher();
        FakeAudit audit = new FakeAudit();
        FixedTimeProvider time = new FixedTimeProvider(
                LocalDate.of(2025, 12, 29),
                Instant.parse("2025-12-29T15:00:00Z")
        );

        CreateUserService service = new CreateUserService(repo, hasher, audit, time);

        CreateUserCommand cmd = new CreateUserCommand(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "Joao",
                "Souza",
                "joao@exemplo.com",
                "SenhaForte123",
                "OutraSenha",
                LocalDate.of(1995, 1, 1)
        );

        assertThrows(UserUseCaseException.class, () -> service.execute(cmd));
        assertTrue(audit.events.stream().anyMatch(e -> !e.success()));
    }

    // -----------------------
    // Fakes (ports) para teste
    // -----------------------

    static class FakeUserRepository implements UserRepositoryPort {
        private final Map<UUID, User> db = new HashMap<>();
        private final Set<String> emails = new HashSet<>();

        void seedEmail(EmailAddress email) {
            emails.add(email.value());
        }

        @Override
        public boolean existsByEmail(EmailAddress email) {
            return emails.contains(email.value());
        }

        @Override
        public User save(User user) {
            db.put(user.id(), user);
            emails.add(user.email().value());
            return user;
        }

        @Override
        public Optional<User> findById(UUID id) {
            return Optional.ofNullable(db.get(id));
        }
    }

    static class FakePasswordHasher implements PasswordHasherPort {
        @Override
        public String hash(String rawPassword) {
            // Hash fake para teste determinístico
            return "HASH(" + rawPassword + ")";
        }
    }

    static class FakeAudit implements AuditPort {
        final List<AuditEvent> events = new CopyOnWriteArrayList<>();

        @Override
        public void publish(AuditEvent event) {
            events.add(event);
        }
    }

    static class FixedTimeProvider implements TimeProvider {
        private final LocalDate today;
        private final Instant now;

        FixedTimeProvider(LocalDate today, Instant now) {
            this.today = today;
            this.now = now;
        }

        @Override
        public Instant nowInstant() {
            return now;
        }

        @Override
        public LocalDate today() {
            return today;
        }
    }
}
