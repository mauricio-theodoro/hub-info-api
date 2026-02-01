package br.com.hubinfo.captcha.adapter.out.persistence;

import br.com.hubinfo.captcha.domain.CaptchaChallengeStatus;
import br.com.hubinfo.captcha.usecase.port.CaptchaChallengeRepositoryPort;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapter de persistência do desafio de CAPTCHA.
 *
 * Responsabilidade:
 * - Gravar e consultar desafios.
 * - Marcar como resolvido com token e timestamp.
 */
@Component
public class CaptchaChallengePersistenceAdapter implements CaptchaChallengeRepositoryPort {

    private final SpringDataCaptchaChallengeRepository repository;

    public CaptchaChallengePersistenceAdapter(SpringDataCaptchaChallengeRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<CaptchaChallengeView> findById(UUID id) {
        return repository.findById(id).map(CaptchaChallengePersistenceAdapter::toView);
    }

    @Override
    public UUID create(UUID serviceRequestId,
                       String cnpj,
                       UUID createdByUserId,
                       String createdByEmail,
                       String pageUrl,
                       String siteKey,
                       String provider,
                       String contextKey,
                       CaptchaChallengeStatus status,
                       Instant createdAt) {

        UUID id = UUID.randomUUID();

        CaptchaChallengeJpaEntity e = new CaptchaChallengeJpaEntity();
        e.setId(id);

        e.setServiceRequestId(serviceRequestId);
        e.setCnpj(cnpj);

        e.setProvider(provider);
        e.setSiteKey(siteKey);
        e.setPageUrl(pageUrl);
        e.setContextKey(contextKey);

        e.setStatus(status.name()); // "PENDING" / "SOLVED"
        e.setCreatedAt(createdAt);

        e.setSolvedAt(null);
        e.setSolutionToken(null);

        // Quem criou o desafio (auditoria operacional)
        e.setCreatedByUserId(createdByUserId);
        e.setCreatedByEmail(createdByEmail);

        repository.save(e);
        return id;
    }

    @Override
    public void markSolved(UUID id, String solutionToken, Instant solvedAt) {
        CaptchaChallengeJpaEntity e = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("CaptchaChallenge não encontrado: " + id));

        e.setStatus(CaptchaChallengeStatus.SOLVED.name());
        e.setSolutionToken(solutionToken);
        e.setSolvedAt(solvedAt);

        repository.save(e);
    }

    private static CaptchaChallengeView toView(CaptchaChallengeJpaEntity e) {
        return new CaptchaChallengeView(
                e.getId(),
                e.getServiceRequestId(),
                e.getCnpj(),
                e.getProvider(),
                e.getSiteKey(),
                e.getPageUrl(),
                e.getContextKey(),
                CaptchaChallengeStatus.valueOf(e.getStatus()),
                e.getCreatedAt(),
                e.getSolvedAt()
        );
    }
}
