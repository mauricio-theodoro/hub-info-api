package br.com.hubinfo.service.usecase.port;

import br.com.hubinfo.service.domain.ServiceRequest;
import br.com.hubinfo.service.domain.ServiceRequestStatus;
import br.com.hubinfo.service.domain.ServiceType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceRequestRepositoryPort {

    ServiceRequest save(ServiceRequest request);

    Optional<ServiceRequest> findById(UUID id);

    /**
     * Lista solicitações com filtros opcionais.
     *
     * Regras de segurança NÃO ficam aqui (infra).
     * Segurança (ADMIN vs ME) fica no caso de uso.
     */
    List<ServiceRequest> findLatest(UUID requestedByUserIdOrNull,
                                    ServiceType serviceTypeOrNull,
                                    ServiceRequestStatus statusOrNull,
                                    int limit);
}