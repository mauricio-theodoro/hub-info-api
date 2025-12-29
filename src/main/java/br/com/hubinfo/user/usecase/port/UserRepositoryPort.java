package br.com.hubinfo.user.usecase.port;

import br.com.hubinfo.user.domain.EmailAddress;
import br.com.hubinfo.user.domain.User;

import java.util.Optional;
import java.util.UUID;

/**
 * Port (gateway) de persistência para User.
 *
 * Observação:
 * - A implementação real será em adapter/out/persistence (JPA).
 * - O caso de uso depende somente desta interface.
 */
public interface UserRepositoryPort {

    boolean existsByEmail(EmailAddress email);

    User save(User user);

    Optional<User> findById(UUID id);
}
