package br.com.hubinfo.service;

import br.com.hubinfo.audit.usecase.RecordAuditEventUseCase;
import br.com.hubinfo.service.usecase.GetServiceRequestUseCase;
import br.com.hubinfo.service.usecase.ListServiceRequestsUseCase;
import br.com.hubinfo.service.usecase.RequestCndUseCase;
import br.com.hubinfo.service.usecase.port.CndGatewayPort;
import br.com.hubinfo.service.usecase.port.ServiceRequestRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class ServiceConfig {

    @Bean
    public RequestCndUseCase requestCndUseCase(ServiceRequestRepositoryPort repository,
                                               CndGatewayPort gateway,
                                               RecordAuditEventUseCase audit,
                                               Clock clock) {
        return new RequestCndUseCase(repository, gateway, audit, clock);
    }

    @Bean
    public GetServiceRequestUseCase getServiceRequestUseCase(ServiceRequestRepositoryPort repository) {
        return new GetServiceRequestUseCase(repository);
    }

    @Bean
    public ListServiceRequestsUseCase listServiceRequestsUseCase(ServiceRequestRepositoryPort repository) {
        return new ListServiceRequestsUseCase(repository);
    }
}
