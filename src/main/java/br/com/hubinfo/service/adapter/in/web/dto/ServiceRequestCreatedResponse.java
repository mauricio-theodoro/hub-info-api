package br.com.hubinfo.service.adapter.in.web.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Resposta padronizada ao criar solicitações.
 */
public record ServiceRequestCreatedResponse(
        UUID id,
        String serviceType,
        String status,
        Instant requestedAt
) {}
