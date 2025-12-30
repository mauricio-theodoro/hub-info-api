package br.com.hubinfo.user.adapter.in.web.admin.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * DTO de saída da criação de usuário.
 *
 * Importante:
 * - Nunca retornar senha/hash.
 */
public record CreateUserResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        Set<String> roles,
        Instant createdAt
) {
}
