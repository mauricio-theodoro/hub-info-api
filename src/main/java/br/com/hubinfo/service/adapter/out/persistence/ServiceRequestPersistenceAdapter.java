package br.com.hubinfo.service.adapter.out.persistence;

import br.com.hubinfo.service.domain.ServiceRequest;
import br.com.hubinfo.service.domain.ServiceRequestStatus;
import br.com.hubinfo.service.domain.ServiceType;
import br.com.hubinfo.service.usecase.port.ServiceRequestRepositoryPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ServiceRequestPersistenceAdapter implements ServiceRequestRepositoryPort {

    private final SpringDataServiceRequestRepository repository;

    public ServiceRequestPersistenceAdapter(SpringDataServiceRequestRepository repository) {
        this.repository = repository;
    }

    @Override
    public ServiceRequest save(ServiceRequest request) {
        ServiceRequestJpaEntity jpa = toJpa(request);
        ServiceRequestJpaEntity saved = repository.save(jpa);
        return toDomain(saved);
    }

    @Override
    public Optional<ServiceRequest> findById(UUID id) {
        return repository.findById(id).map(ServiceRequestPersistenceAdapter::toDomain);
    }

    @Override
    public List<ServiceRequest> findLatest(UUID requestedByUserIdOrNull,
                                           ServiceType serviceTypeOrNull,
                                           ServiceRequestStatus statusOrNull,
                                           int limit) {

        var page = repository.findLatest(
                requestedByUserIdOrNull,
                serviceTypeOrNull == null ? null : serviceTypeOrNull.name(),
                statusOrNull == null ? null : statusOrNull.name(),
                PageRequest.of(0, limit)
        );

        return page.getContent().stream().map(ServiceRequestPersistenceAdapter::toDomain).toList();
    }

    private static ServiceRequestJpaEntity toJpa(ServiceRequest d) {
        ServiceRequestJpaEntity j = new ServiceRequestJpaEntity();
        j.setId(d.id());
        j.setServiceType(d.serviceType().name());
        j.setStatus(d.status().name());
        j.setCnpj(d.cnpj());
        j.setRequestedByUserId(d.requestedByUserId());
        j.setRequestedByEmail(d.requestedByEmail());
        j.setRequestedAt(d.requestedAt());
        j.setCompletedAt(d.completedAt());
        j.setResultCode(d.resultCode());
        j.setResultMessage(d.resultMessage());
        j.setResultPayloadJson(d.resultPayloadJson());
        return j;
    }

    private static ServiceRequest toDomain(ServiceRequestJpaEntity j) {
        return new ServiceRequest(
                j.getId(),
                ServiceType.valueOf(j.getServiceType()),
                ServiceRequestStatus.valueOf(j.getStatus()),
                j.getCnpj(),
                j.getRequestedByUserId(),
                j.getRequestedByEmail(),
                j.getRequestedAt(),
                j.getCompletedAt(),
                j.getResultCode(),
                j.getResultMessage(),
                j.getResultPayloadJson()
        );
    }
}
