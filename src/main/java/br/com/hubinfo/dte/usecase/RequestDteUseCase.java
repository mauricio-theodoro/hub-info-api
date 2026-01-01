package br.com.hubinfo.dte.usecase;

import br.com.hubinfo.service.domain.ServiceRequestStatus;
import br.com.hubinfo.service.domain.ServiceType;

import java.time.Instant;
import java.util.UUID;

/**
 * Caso de uso: registrar uma solicitação de caixa postal (DT-e).
 *
 * Neste commit:
 * - Apenas registra a solicitação com status CAPTCHA_REQUIRED (stub).
 * Próximo commit:
 * - Iniciaremos a automação real (integração/robô) e geração de artefatos.
 */
public interface RequestDteUseCase {

    Result requestFederal(UUID actorUserId, String actorEmail, String cnpj);

    Result requestEstadual(UUID actorUserId, String actorEmail, String cnpj);

    record Result(
            UUID requestId,
            ServiceType serviceType,
            ServiceRequestStatus status,
            Instant requestedAt
    ) {}
}
