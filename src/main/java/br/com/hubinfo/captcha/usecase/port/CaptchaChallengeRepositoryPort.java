package br.com.hubinfo.captcha.usecase.port;

import br.com.hubinfo.captcha.domain.CaptchaChallengeStatus;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Port (interface) para persistência de desafios de CAPTCHA.
 *
 * Contrato:
 * - findById: leitura para UI
 * - create: cria e persiste um desafio novo e retorna o ID
 * - markSolved: marca como resolvido com token e timestamp
 */
public interface CaptchaChallengeRepositoryPort {

    Optional<CaptchaChallengeView> findById(UUID id);

    UUID create(UUID serviceRequestId,
                String cnpj,
                UUID createdByUserId,
                String createdByEmail,
                String pageUrl,
                String siteKey,
                String provider,
                String contextKey,
                CaptchaChallengeStatus status,
                Instant createdAt);

    void markSolved(UUID id, String solutionToken, Instant solvedAt);

    /**
     * View retornada para Controller/UI.
     * Mantém dados necessários para renderizar o hCaptcha e acompanhar status.
     */
    record CaptchaChallengeView(
            UUID id,
            UUID serviceRequestId,
            String cnpj,
            String provider,
            String siteKey,
            String pageUrl,
            String contextKey,
            CaptchaChallengeStatus status,
            Instant createdAt,
            Instant solvedAt
    ) {}
}
