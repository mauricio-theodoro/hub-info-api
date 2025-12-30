package br.com.hubinfo.audit.usecase;

import br.com.hubinfo.audit.domain.AuditEventType;

import java.util.UUID;

public record RecordAuditEventCommand(
        AuditEventType eventType,
        UUID actorUserId,
        String actorEmail,
        String requestIp,
        String requestMethod,
        String requestPath,
        String userAgent,
        Boolean success,
        String targetType,
        UUID targetId,
        String detailsJson
) {
}
