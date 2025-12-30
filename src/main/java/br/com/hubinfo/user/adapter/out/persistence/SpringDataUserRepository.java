package br.com.hubinfo.user.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositório Spring Data (infra).
 *
 * Observação:
 * - Este repositório trabalha com UserJpaEntity.
 * - O domínio e o caso de uso continuam falando com UserRepositoryPort.
 */

public interface SpringDataUserRepository extends JpaRepository<UserJpaEntity, UUID> {

    boolean existsByEmail(String email);

    Optional<UserJpaEntity> findByEmail(String email);
}
