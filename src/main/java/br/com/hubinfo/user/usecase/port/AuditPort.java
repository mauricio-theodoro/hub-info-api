package br.com.hubinfo.user.usecase.port;

import br.com.hubinfo.common.audit.AuditEvent;

/**
 * Port de auditoria.
 *
 * Responsabilidade:
 * - Registrar eventos relevantes (sucesso/falha) do caso de uso.
 *
 * Observação:
 * - Implementaremos a persistência real em commit futuro (tabela audit_event).
 */
public interface AuditPort {
    void publish(AuditEvent event);
}
