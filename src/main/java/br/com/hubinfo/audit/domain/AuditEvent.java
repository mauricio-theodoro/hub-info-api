package br.com.hubinfo.audit.domain;

import java.time.Instant;
import java.util.UUID;

/**
 * Entidade de domínio para auditoria.
 *
 * Observação:
 * - AuditEvent é "append-only": eventos não devem ser alterados após gravados.
 */
public record AuditEvent(
        UUID id,
        AuditEventType eventType,
        Instant occurredAt,
        UUID actorUserId,
        String actorEmail,
        String requestIp,
        String requestMethod,
        String requestPath,
        String userAgent,
        Boolean success,
        String targetType,
        UUID targetId,
        String detailsJson
) {
}
