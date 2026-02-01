package br.com.hubinfo.cnpj.usecase;

import br.com.hubinfo.service.domain.ServiceRequestStatus;
import br.com.hubinfo.service.domain.ServiceType;

import java.time.Instant;
import java.util.UUID;

public interface RequestCnpjDataUseCase {

    Result request(UUID actorUserId, String actorEmail, String cnpj);

    record Result(
            UUID requestId,
            ServiceType serviceType,
            ServiceRequestStatus status,
            Instant requestedAt,
            UUID captchaChallengeId
    ) {}
}
