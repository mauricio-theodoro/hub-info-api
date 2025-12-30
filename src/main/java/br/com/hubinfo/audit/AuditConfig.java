package br.com.hubinfo.audit;

import br.com.hubinfo.audit.usecase.RecordAuditEventUseCase;
import br.com.hubinfo.audit.usecase.port.AuditEventRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class AuditConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public RecordAuditEventUseCase recordAuditEventUseCase(AuditEventRepositoryPort repositoryPort, Clock clock) {
        return new RecordAuditEventUseCase(repositoryPort, clock);
    }
}
