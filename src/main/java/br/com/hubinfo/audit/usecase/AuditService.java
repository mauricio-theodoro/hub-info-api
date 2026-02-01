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
     * Auditoria quando uma solicitação de serviço é criada (CND, DT-e, etc.).
     *
     * @param actorUserId ID do usuário autenticado que executou a ação
     * @param actorEmail  E-mail do usuário autenticado
     * @param serviceType Tipo do serviço (enum.name())
     * @param requestId   ID da solicitação criada
     * @param success     Se a operação foi considerada bem sucedida
     */
    void auditServiceRequestCreated(UUID actorUserId,
                                    String actorEmail,
                                    String serviceType,
                                    String requestId,
                                    boolean success);
    
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

    /**
     * Overload de compatibilidade (LEGADO):
     * Alguns pontos do código antigo chamam apenas com requestId (String).
     * Mantemos para não quebrar o build; o ideal é migrar para o método completo.
     */
    default void auditServiceRequestCreated(String requestId) {
        auditServiceRequestCreated(null, null, null, requestId, true);
    }
}


