package br.com.hubinfo.service.usecase;

import br.com.hubinfo.service.domain.ServiceRequestStatus;
import br.com.hubinfo.service.domain.ServiceType;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Caso de uso para registrar solicitações de serviço.
 *
 * Observação:
 * - O contrato principal é o método com ServiceType + payload genérico + status + requestedAt.
 * - Métodos overload (default) existem apenas para compatibilidade com código legado
 *   e devem ser removidos quando todas as classes forem migradas.
 */
public interface ServiceRequestRegister {

    UUID register(UUID actorUserId,
                  String actorEmail,
                  ServiceType type,
                  Map<String, Object> payload,
                  ServiceRequestStatus status,
                  Instant requestedAt);

    /**
     * Overload de compatibilidade (LEGADO):
     * Alguns serviços antigos chamam register(...) passando serviceType como String e CNPJ separado.
     *
     * Estratégia:
     * - Converte serviceTypeName -> enum
     * - Garante "cnpj" dentro do payload
     * - Define status de forma determinística:
     *   - interactiveLikely => CAPTCHA_REQUIRED
     *   - caso contrário => PENDING
     * - requestedAt = now
     */
    default UUID register(UUID actorUserId,
                          String actorEmail,
                          String serviceTypeName,
                          String cnpj,
                          Map<String, String> payload) {

        ServiceType type = ServiceType.valueOf(serviceTypeName);

        // Merge determinístico, garantindo o cnpj dentro do payload.
        Map<String, Object> merged = new LinkedHashMap<>();
        if (payload != null) merged.putAll(payload);
        merged.put("cnpj", cnpj);

        ServiceRequestStatus status = type.isInteractiveLikely()
                ? ServiceRequestStatus.CAPTCHA_REQUIRED
                : ServiceRequestStatus.PENDING;

        return register(actorUserId, actorEmail, type, merged, status, Instant.now());
    }
}
