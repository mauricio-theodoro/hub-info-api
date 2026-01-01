package br.com.hubinfo.dte.usecase.impl;

import br.com.hubinfo.audit.usecase.AuditService;
import br.com.hubinfo.service.domain.ServiceRequestStatus;
import br.com.hubinfo.service.domain.ServiceType;
import br.com.hubinfo.service.usecase.ServiceRequestRegister;
import br.com.hubinfo.dte.usecase.RequestDteUseCase;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Implementação do caso de uso de solicitação DT-e.
 *
 * Responsabilidades:
 * - Validar e registrar a request.
 * - Auditar a operação.
 *
 * Não faz automação real ainda (por CAPTCHA/MFA etc.).
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
        return create(actorUserId, actorEmail, cnpj, ServiceType.DTE_FEDERAL);
    }

    @Override
    public Result requestEstadual(UUID actorUserId, String actorEmail, String cnpj) {
        return create(actorUserId, actorEmail, cnpj, ServiceType.DTE_ESTADUAL);
    }

    private Result create(UUID actorUserId, String actorEmail, String cnpj, ServiceType type) {
        Instant now = Instant.now();

        // Payload inicial padronizado (fácil de auditar e reprocessar depois).
        Map<String, Object> payload = Map.of(
                "cnpj", cnpj
        );

        UUID requestId = register.register(actorUserId, actorEmail, type, payload, ServiceRequestStatus.CAPTCHA_REQUIRED, now);

        auditService.auditServiceRequestCreated(actorUserId, actorEmail, type.name(), requestId.toString(), true);

        return new Result(requestId, type, ServiceRequestStatus.CAPTCHA_REQUIRED, now);
    }
}
