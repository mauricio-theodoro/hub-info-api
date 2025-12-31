package br.com.hubinfo.service.usecase;

import java.util.UUID;

/**
 * Command do caso de uso para solicitar CND.
 *
 * Contém somente dados necessários para execução do caso de uso.
 */
public record RequestCndCommand(
        String cnpj,
        UUID actorUserId,
        String actorEmail,
        String requestIp,
        String requestMethod,
        String requestPath,
        String userAgent
) {
}
