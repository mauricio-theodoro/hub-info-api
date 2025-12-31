package br.com.hubinfo.service.adapter.in.web.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO detalhado da solicitação.
 * Usado em GET /services/requests/{id}.
 *
 * Observação:
 * - Mantemos payloadJson fora do padrão “sempre retornar” por segurança.
 * - Se precisar expor detalhes avançados futuramente, faremos um endpoint específico
 *   com autorização apropriada e sanitização.
 */
public record ServiceRequestDetailsResponse(
        UUID id,
        String serviceType,
        String status,
        String cnpj,
        UUID requestedByUserId,
        String requestedByEmail,
        Instant requestedAt,
        Instant completedAt,
        String resultCode,
        String resultMessage
) {
}
