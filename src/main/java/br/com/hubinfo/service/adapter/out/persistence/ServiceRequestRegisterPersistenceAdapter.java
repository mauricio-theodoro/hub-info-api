package br.com.hubinfo.service.adapter.out.persistence;

import br.com.hubinfo.service.domain.ServiceRequestStatus;
import br.com.hubinfo.service.domain.ServiceType;
import br.com.hubinfo.service.usecase.ServiceRequestRegister;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Adapter de persistência responsável por registrar solicitações de serviço.
 *
 * Clean Architecture:
 * - O usecase (ServiceRequestRegister) é uma porta (interface).
 * - Este adapter (infra/JPA) implementa a porta e grava no banco.
 *
 * Motivo de estar neste package:
 * - A entidade JPA (ServiceRequestJpaEntity) possui construtor protected (padrão JPA),
 *   logo só pode ser instanciada dentro do mesmo package.
 */
@Component
public class ServiceRequestRegisterPersistenceAdapter implements ServiceRequestRegister {

    private final SpringDataServiceRequestRepository repository;

    public ServiceRequestRegisterPersistenceAdapter(SpringDataServiceRequestRepository repository) {
        this.repository = repository;
    }

    @Override
    public UUID register(UUID actorUserId,
                         String actorEmail,
                         ServiceType type,
                         Map<String, Object> payload,
                         ServiceRequestStatus status,
                         Instant requestedAt) {

        // 1) Pelo schema atual, CNPJ é obrigatório (cnpj NOT NULL char(14)).
        String cnpj = extractAndNormalizeCnpj(payload);

        // 2) Cria a entidade JPA e preenche os campos persistidos na tabela service_requests.
        ServiceRequestJpaEntity entity = new ServiceRequestJpaEntity();
        entity.setId(UUID.randomUUID());
        entity.setServiceType(type.name());
        entity.setStatus(status.name());
        entity.setCnpj(cnpj);

        // 3) Rastreabilidade corporativa: quem solicitou (usuário autenticado).
        entity.setRequestedByUserId(actorUserId);
        entity.setRequestedByEmail(actorEmail);

        // 4) Momento do registro.
        entity.setRequestedAt(requestedAt);

        // 5) Campos de resultado ficam nulos no início; serão preenchidos quando houver processamento.
        // entity.setCompletedAt(...)
        // entity.setResultCode(...)
        // entity.setResultMessage(...)
        // entity.setResultPayloadJson(...)

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
