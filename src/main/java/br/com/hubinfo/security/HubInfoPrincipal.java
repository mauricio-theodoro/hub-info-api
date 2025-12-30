package br.com.hubinfo.security;

import java.util.UUID;

/**
 * Principal autenticado do HUB Info.
 *
 * Responsabilidade:
 * - Representar o usuário autenticado no SecurityContext.
 * - Evita consultas extras ao banco apenas para obter o ID do usuário.
 */
public record HubInfoPrincipal(UUID userId, String email) {
}
