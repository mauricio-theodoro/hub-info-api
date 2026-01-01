package br.com.hubinfo.audit.usecase;

import br.com.hubinfo.audit.domain.AuditEventType;

import java.util.Map;
import java.util.UUID;

/**
 * Caso de uso de auditoria (camada de aplicação).
 *
 * Padrão adotado no HUB Info:
 * - A auditoria é "append-only" (não altera eventos já gravados).
 * - O usecase recebe dados de negócio (tipo/ator/alvo/detalhes) e
 *   a implementação enriquece com contexto HTTP quando disponível.
 */
public interface AuditService {

    /**
     * Registra um evento de auditoria.
     *
     * @param eventType  tipo do evento (enum do domínio)
     * @param actorUserId id do usuário executor (pode ser null se não autenticado)
     * @param actorEmail email do executor (pode ser null)
     * @param success   resultado (true/false) - padronize conforme sua regra
     * @param targetType tipo do alvo (ex.: "USER", "SERVICE_REQUEST")
     * @param targetId   UUID do alvo (pode ser null dependendo do evento)
     * @param details    dados extras (serão serializados para JSON em detailsJson)
     */
    void record(AuditEventType eventType,
                UUID actorUserId,
                String actorEmail,
                boolean success,
                String targetType,
                UUID targetId,
                Map<String, Object> details);

    /**
     * Atalho corporativo: auditoria quando uma solicitação de serviço é criada.
     * Isso mantém consistência e evita duplicação nas features (CND/DT-e/etc).
     */
    void serviceRequestCreated(UUID actorUserId,
                               String actorEmail,
                               String serviceType,
                               UUID requestId,
                               boolean success,
                               Map<String, Object> details);
}
