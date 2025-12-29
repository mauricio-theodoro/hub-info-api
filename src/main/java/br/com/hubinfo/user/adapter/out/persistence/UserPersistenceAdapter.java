package br.com.hubinfo.user.adapter.out.persistence;

import br.com.hubinfo.user.domain.EmailAddress;
import br.com.hubinfo.user.domain.Role;
import br.com.hubinfo.user.domain.User;
import br.com.hubinfo.user.usecase.port.UserRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adapter de persistência (JPA) que implementa o port do caso de uso.
 *
 * Clean Architecture:
 * - Use case depende do port (interface).
 * - Adapter depende de Spring/JPA e converte para/da entidade de domínio.
 */
@Component
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final SpringDataUserRepository repository;

    public UserPersistenceAdapter(SpringDataUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean existsByEmail(EmailAddress email) {
        return repository.existsByEmail(email.value());
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = toJpa(user);
        UserJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    private UserJpaEntity toJpa(User user) {
        UserJpaEntity entity = new UserJpaEntity();
        entity.setId(user.id());
        entity.setFirstName(user.firstName());
        entity.setLastName(user.lastName());
        entity.setEmail(user.email().value());
        entity.setPasswordHash(user.passwordHash());
        entity.setBirthDate(user.birthDate());
        entity.setRolesCsv(toRolesCsv(user.roles()));
        entity.setCreatedAt(user.createdAt());
        return entity;
    }

    private User toDomain(UserJpaEntity entity) {
        return User.rehydrate(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                EmailAddress.of(entity.getEmail()),
                entity.getPasswordHash(),
                entity.getBirthDate(),
                parseRoles(entity.getRolesCsv()),
                entity.getCreatedAt()
        );
    }

    private static String toRolesCsv(Set<Role> roles) {
        return roles.stream()
                .map(Enum::name)
                .sorted()
                .collect(Collectors.joining(","));
    }

    private static Set<Role> parseRoles(String rolesCsv) {
        if (rolesCsv == null || rolesCsv.isBlank()) {
            return Set.of();
        }
        return Arrays.stream(rolesCsv.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(Role::valueOf)
                .collect(Collectors.toUnmodifiableSet());
    }
}
