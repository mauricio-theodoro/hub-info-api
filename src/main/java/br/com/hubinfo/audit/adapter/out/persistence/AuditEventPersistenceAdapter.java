package br.com.hubinfo.audit.adapter.out.persistence;

import br.com.hubinfo.audit.domain.AuditEvent;
import br.com.hubinfo.audit.domain.AuditEventType;
import br.com.hubinfo.audit.usecase.port.AuditEventRepositoryPort;
import org.springframework.stereotype.Component;

@Component
public class AuditEventPersistenceAdapter implements AuditEventRepositoryPort {

    private final SpringDataAuditEventRepository repository;

    public AuditEventPersistenceAdapter(SpringDataAuditEventRepository repository) {
        this.repository = repository;
    }

    @Override
    public AuditEvent save(AuditEvent event) {
        AuditEventJpaEntity entity = toJpa(event);
        AuditEventJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    private static AuditEventJpaEntity toJpa(AuditEvent e) {
        AuditEventJpaEntity j = new AuditEventJpaEntity();
        j.setId(e.id());
        j.setEventType(e.eventType().name());
        j.setOccurredAt(e.occurredAt());
        j.setActorUserId(e.actorUserId());
        j.setActorEmail(e.actorEmail());
        j.setRequestIp(e.requestIp());
        j.setRequestMethod(e.requestMethod());
        j.setRequestPath(e.requestPath());
        j.setUserAgent(e.userAgent());
        j.setSuccess(e.success());
        j.setTargetType(e.targetType());
        j.setTargetId(e.targetId());
        j.setDetailsJson(e.detailsJson());
        return j;
    }

    private static AuditEvent toDomain(AuditEventJpaEntity j) {
        return new AuditEvent(
                j.getId(),
                AuditEventType.valueOf(j.getEventType()),
                j.getOccurredAt(),
                j.getActorUserId(),
                j.getActorEmail(),
                j.getRequestIp(),
                j.getRequestMethod(),
                j.getRequestPath(),
                j.getUserAgent(),
                j.getSuccess(),
                j.getTargetType(),
                j.getTargetId(),
                j.getDetailsJson()
        );
    }
}
