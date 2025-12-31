package br.com.hubinfo.service.adapter.in.web.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO “enxuto” para listagens (tela de histórico).
 */
public record ServiceRequestListResponse(
        UUID id,
        String serviceType,
        String status,
        String cnpj,
        Instant requestedAt,
        Instant completedAt,
        String resultCode
) {
}
