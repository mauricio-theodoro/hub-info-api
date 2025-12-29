package br.com.hubinfo.user.usecase.create;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * Modelo de saída do caso de uso.
 *
 * Observação:
 * - Nunca retornamos senha/hash.
 * - Retornamos dados úteis para UI/auditoria.
 */
public record CreateUserResult(
        UUID userId,
        String firstName,
        String lastName,
        String email,
        Set<String> roles,
        Instant createdAt
) {
}
