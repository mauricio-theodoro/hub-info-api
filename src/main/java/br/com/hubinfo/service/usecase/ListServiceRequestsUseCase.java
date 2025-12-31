package br.com.hubinfo.service.usecase;

import br.com.hubinfo.service.domain.ServiceRequest;
import br.com.hubinfo.service.domain.ServiceRequestStatus;
import br.com.hubinfo.service.domain.ServiceType;
import br.com.hubinfo.service.usecase.port.ServiceRequestRepositoryPort;

import java.util.List;
import java.util.UUID;

/**
 * Caso de uso: listar solicitações.
 *
 * Escopos:
 * - ME  -> lista somente do usuário
 * - ALL -> somente ADMIN (lista geral)
 */
public class ListServiceRequestsUseCase {

    public enum Scope { ME, ALL }

    private final ServiceRequestRepositoryPort repository;

    public ListServiceRequestsUseCase(ServiceRequestRepositoryPort repository) {
        this.repository = repository;
    }

    public List<ServiceRequest> list(UUID actorUserId,
                                     boolean isAdmin,
                                     Scope scope,
                                     ServiceType serviceType,
                                     ServiceRequestStatus status,
                                     int limit) {

        int safeLimit = Math.min(Math.max(limit, 1), 100);

        // Scope ALL é restrito.
        if (scope == Scope.ALL && !isAdmin) {
            throw new IllegalArgumentException("Acesso negado.");
        }

        UUID requestedBy = (scope == Scope.ALL) ? null : actorUserId;

        return repository.findLatest(requestedBy, serviceType, status, safeLimit);
    }
}
