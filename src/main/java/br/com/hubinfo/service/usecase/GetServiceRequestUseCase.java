package br.com.hubinfo.service.usecase;

import br.com.hubinfo.service.domain.ServiceRequest;
import br.com.hubinfo.service.usecase.port.ServiceRequestRepositoryPort;

import java.util.UUID;

/**
 * Caso de uso: consultar uma solicitação por ID com controle de acesso.
 *
 * Regras:
 * - ADMIN pode ver qualquer solicitação.
 * - USER só pode ver se requestedByUserId == actorUserId.
 */
public class GetServiceRequestUseCase {

    private final ServiceRequestRepositoryPort repository;

    public GetServiceRequestUseCase(ServiceRequestRepositoryPort repository) {
        this.repository = repository;
    }

    public ServiceRequest getById(UUID requestId, UUID actorUserId, boolean isAdmin) {
        ServiceRequest req = repository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada."));

        // Se não for admin, só pode acessar o que é dele.
        if (!isAdmin && (req.requestedByUserId() == null || !req.requestedByUserId().equals(actorUserId))) {
            // Sem vazar informação: mantemos mensagem neutra.
            throw new IllegalArgumentException("Solicitação não encontrada.");
        }

        return req;
    }
}
