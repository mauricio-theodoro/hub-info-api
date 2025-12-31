package br.com.hubinfo.service.adapter.in.web.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO de saída padronizado para solicitações.
 */
public record ServiceRequestResponse(
        UUID id,
        String serviceType,
        String status,
        String cnpj,
        Instant requestedAt,
        Instant completedAt,
        String resultCode,
        String resultMessage
) {
}
