package br.com.hubinfo.common.audit;

/**
 * Tipos de eventos de auditoria do sistema.
 *
 * Observação:
 * - Mantemos os tipos em módulo comum para serem reutilizados por outros features (company/services/etc).
 */
public enum AuditEventType {
    USER_CREATED,
    USER_CREATE_FAILED
}
