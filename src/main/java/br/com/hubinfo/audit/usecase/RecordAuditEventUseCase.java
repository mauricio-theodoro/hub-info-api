package br.com.hubinfo.audit.usecase;

import br.com.hubinfo.audit.domain.AuditEvent;
import br.com.hubinfo.audit.usecase.port.AuditEventRepositoryPort;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

/**
 * Caso de uso: registrar evento de auditoria.
 *
 * Responsabilidade:
 * - Gerar ID/Instant
 * - Persistir via port
 */
public class RecordAuditEventUseCase {

    private final AuditEventRepositoryPort repository;
    private final Clock clock;

    public RecordAuditEventUseCase(AuditEventRepositoryPort repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    public AuditEvent record(RecordAuditEventCommand cmd) {
        Instant now = Instant.now(clock);

        AuditEvent event = new AuditEvent(
                UUID.randomUUID(),
                cmd.eventType(),
                now,
                cmd.actorUserId(),
                cmd.actorEmail(),
                cmd.requestIp(),
                cmd.requestMethod(),
                cmd.requestPath(),
                cmd.userAgent(),
                cmd.success(),
                cmd.targetType(),
                cmd.targetId(),
                cmd.detailsJson()
        );

        return repository.save(event);
    }
}
