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
 */
@Service
public class AuditServiceImpl implements AuditService {

    private final AuditEventRepositoryPort repository;
    private final ObjectMapper objectMapper;

    public AuditServiceImpl(AuditEventRepositoryPort repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

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

    @Override
    public void serviceRequestCreated(UUID actorUserId,
                                      String actorEmail,
                                      String serviceType,
                                      UUID requestId,
                                      boolean success,
                                      Map<String, Object> details) {

        Map<String, Object> enriched = details == null ? Map.of(
                "serviceType", serviceType
        ) : merge(details, Map.of("serviceType", serviceType));

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

    private String toJson(Map<String, Object> details) {
        try {
            if (details == null || details.isEmpty()) return null;
            return objectMapper.writeValueAsString(details);
        } catch (Exception ex) {
            // Auditoria não pode gerar dados inconsistentes silenciosamente.
            throw new IllegalStateException("Falha ao serializar detailsJson do AuditEvent.", ex);
        }
    }

    private static Map<String, Object> merge(Map<String, Object> a, Map<String, Object> b) {
        // Merge simples e determinístico para manter rastreabilidade.
        var merged = new java.util.LinkedHashMap<String, Object>();
        merged.putAll(a);
        merged.putAll(b);
        return java.util.Collections.unmodifiableMap(merged);
    }

    /**
     * Representa o contexto HTTP disponível no momento do evento.
     * Se não existir request ativo, todos os campos serão null.
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

        private static String resolveClientIp(HttpServletRequest req) {
            // Suporte básico a proxy (quando houver).
            String xff = req.getHeader("X-Forwarded-For");
            if (xff != null && !xff.isBlank()) {
                // pega o primeiro IP da lista
                int comma = xff.indexOf(',');
                return (comma > 0 ? xff.substring(0, comma) : xff).trim();
            }
            return req.getRemoteAddr();
        }
    }
}
