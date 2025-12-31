package br.com.hubinfo.service.usecase.port;

import br.com.hubinfo.service.domain.ServiceRequest;

import java.util.Optional;
import java.util.UUID;

public interface ServiceRequestRepositoryPort {
    ServiceRequest save(ServiceRequest request);
    Optional<ServiceRequest> findById(UUID id);
}
