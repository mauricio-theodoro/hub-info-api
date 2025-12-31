package br.com.hubinfo.service.usecase;

import br.com.hubinfo.audit.domain.AuditEventType;
import br.com.hubinfo.audit.usecase.RecordAuditEventCommand;
import br.com.hubinfo.audit.usecase.RecordAuditEventUseCase;
import br.com.hubinfo.service.domain.ServiceRequest;
import br.com.hubinfo.service.domain.ServiceRequestStatus;
import br.com.hubinfo.service.domain.ServiceType;
import br.com.hubinfo.service.usecase.port.CndGatewayPort;
import br.com.hubinfo.service.usecase.port.ServiceRequestRepositoryPort;

import java.time.Clock;
import java.time.Instant;

/**
 * Caso de uso: Solicitar CND para um CNPJ.
 *
 * Fluxo:
 * 1) Normaliza/valida CNPJ
 * 2) Cria ServiceRequest PENDING e persiste
 * 3) Audita SERVICE_REQUESTED
 * 4) Chama gateway externo (por enquanto stub)
 * 5) Atualiza request para SUCCESS/FAILURE e persiste
 * 6) Audita SERVICE_REQUEST_SUCCESS / SERVICE_REQUEST_FAILURE
 */
public class RequestCndUseCase {

    private final ServiceRequestRepositoryPort repository;
    private final CndGatewayPort cndGateway;
    private final RecordAuditEventUseCase audit;
    private final Clock clock;

    public RequestCndUseCase(ServiceRequestRepositoryPort repository,
                             CndGatewayPort cndGateway,
                             RecordAuditEventUseCase audit,
                             Clock clock) {
        this.repository = repository;
        this.cndGateway = cndGateway;
        this.audit = audit;
        this.clock = clock;
    }

    public ServiceRequest request(RequestCndCommand cmd) {
        Instant now = Instant.now(clock);

        String normalizedCnpj = normalizeCnpj(cmd.cnpj());

        // 1) cria request PENDING
        ServiceRequest pending = ServiceRequest.createPending(
                ServiceType.CND,
                normalizedCnpj,
                cmd.actorUserId(),
                cmd.actorEmail(),
                now
        );

        // 2) persiste PENDING
        ServiceRequest savedPending = repository.save(pending);

        // 3) audita “solicitado”
        audit.record(new RecordAuditEventCommand(
                AuditEventType.SERVICE_REQUESTED,
                cmd.actorUserId(),
                cmd.actorEmail(),
                cmd.requestIp(),
                cmd.requestMethod(),
                cmd.requestPath(),
                cmd.userAgent(),
                true,
                "SERVICE_REQUEST",
                savedPending.id(),
                "{\"serviceType\":\"CND\",\"cnpj\":\"" + normalizedCnpj + "\"}"
        ));

        // 4) chama gateway (stub por enquanto)
        var result = cndGateway.requestCnd(normalizedCnpj);

        // 5) completa conforme resultado
        ServiceRequestStatus finalStatus = result.success()
                ? ServiceRequestStatus.SUCCESS
                : ServiceRequestStatus.FAILURE;

        ServiceRequest completed = savedPending.complete(
                finalStatus,
                Instant.now(clock),
                result.resultCode(),
                result.message(),
                result.payloadJson()
        );

        ServiceRequest savedCompleted = repository.save(completed);

        // 6) audita sucesso/falha
        audit.record(new RecordAuditEventCommand(
                result.success() ? AuditEventType.SERVICE_REQUEST_SUCCESS : AuditEventType.SERVICE_REQUEST_FAILURE,
                cmd.actorUserId(),
                cmd.actorEmail(),
                cmd.requestIp(),
                cmd.requestMethod(),
                cmd.requestPath(),
                cmd.userAgent(),
                result.success(),
                "SERVICE_REQUEST",
                savedCompleted.id(),
                "{\"serviceType\":\"CND\",\"resultCode\":\"" + result.resultCode() + "\"}"
        ));

        return savedCompleted;
    }

    /**
     * Normaliza CNPJ removendo símbolos e garantindo 14 dígitos.
     * Regra simples neste commit; podemos evoluir para validação completa de dígitos verificadores depois.
     */
    private static String normalizeCnpj(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("CNPJ é obrigatório.");
        }

        String digits = raw.replaceAll("\\D", "");
        if (digits.length() != 14) {
            throw new IllegalArgumentException("CNPJ inválido. Deve conter 14 dígitos.");
        }

        return digits;
    }
}
