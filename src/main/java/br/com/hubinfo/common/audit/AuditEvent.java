package br.com.hubinfo.common.audit;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Modelo comum de auditoria (agnóstico a banco / framework).
 *
 * Responsabilidade:
 * - Transportar informações mínimas e úteis sobre o que ocorreu no sistema.
 *
 * Onde é persistido?
 * - A persistência (JPA, tabela audit_event, etc.) será implementada em adapter/out no commit de auditoria.
 */
public record AuditEvent(
        UUID actorUserId,
        AuditEventType type,
        Instant timestamp,
        String reference,
        boolean success,
        Map<String, Object> details
) {
}
