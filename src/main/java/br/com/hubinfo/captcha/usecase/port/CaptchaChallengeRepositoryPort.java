package br.com.hubinfo.captcha.usecase.port;

import br.com.hubinfo.captcha.domain.CaptchaChallengeStatus;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface CaptchaChallengeRepositoryPort {

    UUID create(UUID serviceRequestId,
                String cnpj,
                UUID createdByUserId,
                String createdByEmail,
                String pageUrl,
                String siteKey,
                String provider,
                CaptchaChallengeStatus status,
                Instant createdAt);

    Optional<CaptchaChallengeView> findById(UUID id);

    void markSolved(UUID id, String solutionToken, Instant solvedAt);

    /**
     * Projeção de leitura (evita expor entidade JPA).
     */
    record CaptchaChallengeView(
            UUID id,
            UUID serviceRequestId,
            String cnpj,
            String provider,
            String pageUrl,
            String siteKey,
            CaptchaChallengeStatus status,
            Instant createdAt,
            Instant solvedAt
    ) {}
}
