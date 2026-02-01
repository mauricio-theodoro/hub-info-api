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
 * Implementação do caso de uso de registro de solicitações de serviço.
 *
 * Observação de arquitetura (pragmática para o momento):
 * - Este serviço escreve diretamente via Spring Data JPA para persistir a request.
 * - A tabela atual exige cnpj (NOT NULL), então o registrador extrai isso do payload.
 *
 * Futuro (quando você criar request_payload_json ou separar melhor camadas):
 * - Este UseCase pode depender apenas de um Port e um Adapter persistir.
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

        // 1) Pelo schema atual, CNPJ é obrigatório.
        String cnpj = extractAndNormalizeCnpj(payload);

        // 2) Cria a entidade JPA usando factory interna (evita problema com construtor protected).
        ServiceRequestJpaEntity entity = ServiceRequestJpaEntity.newRequest(
                UUID.randomUUID(),
                type.name(),
                status.name(),
                cnpj,
                actorUserId,
                actorEmail,
                requestedAt
        );

        // 3) Persiste e retorna o id.
        return repository.save(entity).getId();
    }

    /**
     * Extrai e normaliza CNPJ para exatamente 14 dígitos.
     *
     * Regras:
     * - Campo "cnpj" precisa existir no payload.
     * - Remove máscara e qualquer caractere não numérico.
     * - Valida tamanho final (14).
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
