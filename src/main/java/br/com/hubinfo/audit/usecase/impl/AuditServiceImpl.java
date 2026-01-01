package br.com.hubinfo.audit.usecase.impl;

import br.com.hubinfo.audit.domain.AuditEvent;
import br.com.hubinfo.audit.domain.AuditEventType;
import br.com.hubinfo.audit.usecase.AuditService;
import br.com.hubinfo.audit.usecase.port.AuditEventRepositoryPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Implementação do caso de uso de auditoria.
 *
 * Estratégia:
 * - Cria AuditEvent (domínio).
 * - Enriquece com contexto HTTP (IP, método, path, user-agent) quando existir.
 * - Serializa details -> detailsJson para armazenar no banco.
 *
 * Observação de evolução do projeto:
 * - O método "oficial" para registrar criação de solicitações é serviceRequestCreated(...).
 * - Para manter compatibilidade com outras classes que chamam auditServiceRequestCreated(...),
 *   existe um método delegador com esse nome.
 */
@Service
public class AuditServiceImpl implements AuditService {

    private final AuditEventRepositoryPort repository;
    private final ObjectMapper objectMapper;

    public AuditServiceImpl(AuditEventRepositoryPort repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    /**
     * Método base e genérico para registrar eventos de auditoria.
     *
     * Regras:
     * - Auditoria é append-only (somente inserção).
     * - O evento deve conter o máximo de contexto possível, mas nunca deve
     *   quebrar o fluxo por ausência de request HTTP.
     */
    @Override
    public void record(AuditEventType eventType,
                       UUID actorUserId,
                       String actorEmail,
                       boolean success,
                       String targetType,
                       UUID targetId,
                       Map<String, Object> details) {

        HttpContext ctx = HttpContext.fromCurrentRequest();

        AuditEvent event = new AuditEvent(
                UUID.randomUUID(),
                eventType,
                Instant.now(),
                actorUserId,
                actorEmail,
                ctx.ip(),
                ctx.method(),
                ctx.path(),
                ctx.userAgent(),
                success,
                targetType,
                targetId,
                toJson(details)
        );

        repository.save(event);
    }

    /**
     * Auditoria padronizada para criação de solicitações (CND, DT-e etc.).
     *
     * @param actorUserId ID do usuário que criou a solicitação
     * @param actorEmail  e-mail do usuário que criou a solicitação
     * @param serviceType tipo do serviço (enum.name())
     * @param requestId   ID da service_request criada
     * @param success     se a operação foi considerada bem sucedida
     * @param details     detalhes extras (opcional)
     */
    @Override
    public void serviceRequestCreated(UUID actorUserId,
                                      String actorEmail,
                                      String serviceType,
                                      UUID requestId,
                                      boolean success,
                                      Map<String, Object> details) {

        Map<String, Object> enriched = details == null
                ? Map.of("serviceType", serviceType)
                : merge(details, Map.of("serviceType", serviceType));

        record(
                AuditEventType.SERVICE_REQUEST_CREATED,
                actorUserId,
                actorEmail,
                success,
                "SERVICE_REQUEST",
                requestId,
                enriched
        );
    }

    /**
     * Método de compatibilidade com chamadas existentes no projeto.
     *
     * Cenário:
     * - Algumas classes chamam auditServiceRequestCreated(..., requestId como String).
     * - O método padrão interno trabalha com UUID.
     *
     * Estratégia:
     * - Tenta converter requestId (String) para UUID.
     * - Se converter, delega para serviceRequestCreated(...).
     * - Se não converter, registra o evento com success=false e guarda o requestId inválido em details.
     */
    @Override
    public void auditServiceRequestCreated(UUID actorUserId,
                                           String actorEmail,
                                           String serviceType,
                                           String requestId,
                                           boolean success) {

        UUID parsedRequestId;
        try {
            parsedRequestId = UUID.fromString(requestId);
        } catch (Exception ex) {
            // Não derruba o sistema por causa de auditoria: registra evento com indicação do problema.
            record(
                    AuditEventType.SERVICE_REQUEST_CREATED,
                    actorUserId,
                    actorEmail,
                    false,
                    "SERVICE_REQUEST",
                    null,
                    Map.of(
                            "serviceType", serviceType,
                            "invalidRequestId", requestId
                    )
            );
            return;
        }

        serviceRequestCreated(
                actorUserId,
                actorEmail,
                serviceType,
                parsedRequestId,
                success,
                null
        );
    }

    /**
     * Serializa o mapa de detalhes em JSON para persistência.
     *
     * Regra:
     * - Se details for vazio/nulo, retorna null para economizar espaço e manter semântica.
     * - Se falhar a serialização, lança exceção: auditoria não pode gravar dados corrompidos.
     */
    private String toJson(Map<String, Object> details) {
        try {
            if (details == null || details.isEmpty()) return null;
            return objectMapper.writeValueAsString(details);
        } catch (Exception ex) {
            throw new IllegalStateException("Falha ao serializar detailsJson do AuditEvent.", ex);
        }
    }

    /**
     * Merge simples e determinístico:
     * - Primeiro "a", depois "b" sobrescreve chaves repetidas.
     * - LinkedHashMap mantém ordem (útil para logs e rastreabilidade).
     */
    private static Map<String, Object> merge(Map<String, Object> a, Map<String, Object> b) {
        var merged = new java.util.LinkedHashMap<String, Object>();
        merged.putAll(a);
        merged.putAll(b);
        return java.util.Collections.unmodifiableMap(merged);
    }

    /**
     * Representa o contexto HTTP disponível no momento do evento.
     *
     * Se não existir request ativo (ex.: execução fora do fluxo web),
     * todos os campos retornam null e a auditoria continua funcionando.
     */
    private record HttpContext(String ip, String method, String path, String userAgent) {

        static HttpContext fromCurrentRequest() {
            try {
                var attrs = RequestContextHolder.getRequestAttributes();
                if (!(attrs instanceof ServletRequestAttributes sra)) {
                    return new HttpContext(null, null, null, null);
                }
                HttpServletRequest req = sra.getRequest();
                return new HttpContext(
                        resolveClientIp(req),
                        req.getMethod(),
                        req.getRequestURI(),
                        req.getHeader("User-Agent")
                );
            } catch (Exception ignored) {
                return new HttpContext(null, null, null, null);
            }
        }

        /**
         * Resolve IP do cliente considerando proxy reverso (quando existir).
         * - Se houver X-Forwarded-For, usa o primeiro IP da lista.
         * - Caso contrário, usa getRemoteAddr().
         */
        private static String resolveClientIp(HttpServletRequest req) {
            String xff = req.getHeader("X-Forwarded-For");
            if (xff != null && !xff.isBlank()) {
                int comma = xff.indexOf(',');
                return (comma > 0 ? xff.substring(0, comma) : xff).trim();
            }
            return req.getRemoteAddr();
        }
    }
}
