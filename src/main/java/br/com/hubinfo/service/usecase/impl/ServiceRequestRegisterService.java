package br.com.hubinfo.service.usecase.impl;

import br.com.hubinfo.service.adapter.out.persistence.ServiceRequestJpaEntity;
import br.com.hubinfo.service.adapter.out.persistence.SpringDataServiceRequestRepository;
import br.com.hubinfo.service.domain.ServiceRequestStatus;
import br.com.hubinfo.service.domain.ServiceType;
import br.com.hubinfo.service.usecase.ServiceRequestRegister;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Caso de uso: registrar solicitações de serviços.
 *
 * Nota:
 * - O schema atual exige CNPJ (NOT NULL).
 * - Ainda não temos "request_payload_json" no banco para guardar payload genérico.
 * - Portanto extraímos CNPJ do payload e persistimos campos base.
 */
@Service
public class ServiceRequestRegisterService implements ServiceRequestRegister {

    private final SpringDataServiceRequestRepository repository;

    public ServiceRequestRegisterService(SpringDataServiceRequestRepository repository) {
        this.repository = repository;
    }

    @Override
    public UUID register(UUID actorUserId,
                         String actorEmail,
                         ServiceType type,
                         Map<String, Object> payload,
                         ServiceRequestStatus status,
                         Instant requestedAt) {

        // 1) CNPJ obrigatório pelo schema atual.
        String cnpj = extractAndNormalizeCnpj(payload);

        // 2) Criação da entidade via factory (evita o problema do construtor protected).
        UUID requestId = UUID.randomUUID();
        ServiceRequestJpaEntity entity = ServiceRequestJpaEntity.newRequest(
                requestId,
                type.name(),
                status.name(),
                cnpj,
                actorUserId,
                actorEmail,
                requestedAt
        );

        repository.save(entity);
        return requestId;
    }

    /**
     * Extrai e normaliza CNPJ (14 dígitos).
     */
    private static String extractAndNormalizeCnpj(Map<String, Object> payload) {
        if (payload == null) {
            throw new IllegalArgumentException("Payload inválido: não pode ser nulo.");
        }
        Object raw = payload.get("cnpj");
        if (raw == null) {
            throw new IllegalArgumentException("Payload inválido: campo 'cnpj' é obrigatório.");
        }
        String digits = raw.toString().replaceAll("\\D", "");
        if (digits.length() != 14) {
            throw new IllegalArgumentException("CNPJ inválido: deve conter 14 dígitos.");
        }
        return digits;
    }
}
