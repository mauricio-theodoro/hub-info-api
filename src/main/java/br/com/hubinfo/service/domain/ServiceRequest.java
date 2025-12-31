package br.com.hubinfo.service.domain;

import java.time.Instant;
import java.util.UUID;

/**
 * Entidade de domínio para uma solicitação de serviço.
 *
 * Observação:
 * - O domínio não conhece JPA, Spring ou Web.
 * - Ele representa o conceito do negócio: "um usuário solicitou um serviço para um CNPJ".
 */
public record ServiceRequest(
        UUID id,
        ServiceType serviceType,
        ServiceRequestStatus status,
        String cnpj,                 // sempre normalizado (somente dígitos, 14 chars)
        UUID requestedByUserId,
        String requestedByEmail,
        Instant requestedAt,
        Instant completedAt,
        String resultCode,
        String resultMessage,
        String resultPayloadJson
) {

    /**
     * Fábrica para criar uma solicitação inicial.
     * Mantém invariantes: status inicial PENDING e cnpj normalizado.
     */
    public static ServiceRequest createPending(ServiceType type,
                                               String normalizedCnpj,
                                               UUID actorUserId,
                                               String actorEmail,
                                               Instant now) {
        return new ServiceRequest(
                UUID.randomUUID(),
                type,
                ServiceRequestStatus.PENDING,
                normalizedCnpj,
                actorUserId,
                actorEmail,
                now,
                null,
                null,
                null,
                null
        );
    }

    /**
     * Retorna uma cópia concluída (SUCCESS/FAILURE).
     * Mantém imutabilidade e evita mutação espalhada.
     */
    public ServiceRequest complete(ServiceRequestStatus newStatus,
                                   Instant completedAt,
                                   String resultCode,
                                   String resultMessage,
                                   String resultPayloadJson) {
        return new ServiceRequest(
                this.id,
                this.serviceType,
                newStatus,
                this.cnpj,
                this.requestedByUserId,
                this.requestedByEmail,
                this.requestedAt,
                completedAt,
                resultCode,
                resultMessage,
                resultPayloadJson
        );
    }
}
