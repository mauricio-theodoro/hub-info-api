package br.com.hubinfo.dte.usecase.impl;

import br.com.hubinfo.audit.usecase.AuditService;
import br.com.hubinfo.dte.usecase.RequestDteUseCase;
import br.com.hubinfo.service.domain.ServiceRequestStatus;
import br.com.hubinfo.service.domain.ServiceType;
import br.com.hubinfo.service.usecase.ServiceRequestRegister;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Implementação do caso de uso de solicitação de DT-e (caixa postal).
 *
 * Responsabilidades:
 * - Registrar a solicitação no banco (service_requests).
 * - Auditar o evento "SERVICE_REQUEST_CREATED".
 *
 * Observação:
 * - Ainda não faz automação real por conta de CAPTCHA/MFA etc.
 * - O status inicial padrão é CAPTCHA_REQUIRED.
 */
@Service
public class RequestDteService implements RequestDteUseCase {

    private final ServiceRequestRegister register;
    private final AuditService auditService;

    public RequestDteService(ServiceRequestRegister register, AuditService auditService) {
        this.register = register;
        this.auditService = auditService;
    }

    @Override
    public Result requestFederal(UUID actorUserId, String actorEmail, String cnpj) {
        return create(actorUserId, actorEmail, cnpj, ServiceType.DTE_CAIXA_POSTAL_FEDERAL);
    }

    @Override
    public Result requestEstadual(UUID actorUserId, String actorEmail, String cnpj) {
        return create(actorUserId, actorEmail, cnpj, ServiceType.DTE_CAIXA_POSTAL_ESTADUAL);
    }

    private Result create(UUID actorUserId, String actorEmail, String cnpj, ServiceType type) {
        Instant now = Instant.now();

        // Payload mínimo padronizado para rastreabilidade (o registrador extrai o CNPJ daqui).
        Map<String, Object> payload = Map.of("cnpj", cnpj);

        UUID requestId = register.register(
                actorUserId,
                actorEmail,
                type,
                payload,
                ServiceRequestStatus.CAPTCHA_REQUIRED,
                now
        );

        // Auditoria: grava um evento indicando que a solicitação foi criada.
        auditService.auditServiceRequestCreated(
                actorUserId,
                actorEmail,
                type.name(),
                requestId.toString(),
                true
        );

        return new Result(requestId, type, ServiceRequestStatus.CAPTCHA_REQUIRED, now);
    }
}
