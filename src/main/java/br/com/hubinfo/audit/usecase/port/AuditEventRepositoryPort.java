package br.com.hubinfo.audit.usecase.port;

import br.com.hubinfo.audit.domain.AuditEvent;

public interface AuditEventRepositoryPort {
    AuditEvent save(AuditEvent event);
}
