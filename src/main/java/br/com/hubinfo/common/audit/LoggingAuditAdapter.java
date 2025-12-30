package br.com.hubinfo.common.audit;

import br.com.hubinfo.user.usecase.port.AuditPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Adapter provisório de auditoria (somente log).
 *
 * Motivo:
 * - Permitir evoluir o sistema por commits pequenos.
 * - A persistência em tabela audit_event será implementada em um commit próprio.
 *
 * Importante:
 * - NÃO registrar dados sensíveis (senha, hash).
 * - Registrar apenas metadados mínimos.
 */
@Component
public class LoggingAuditAdapter implements AuditPort {

    private static final Logger log = LoggerFactory.getLogger(LoggingAuditAdapter.class);

    @Override
    public void publish(AuditEvent event) {
        log.info("AUDIT type={} success={} actor={} ref={} details={}",
                event.type(), event.success(), event.actorUserId(), event.reference(), event.details());
    }
}
