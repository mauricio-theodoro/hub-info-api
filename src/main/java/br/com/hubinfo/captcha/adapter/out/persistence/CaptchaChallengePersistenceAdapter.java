package br.com.hubinfo.captcha.adapter.out.persistence;

import br.com.hubinfo.captcha.domain.CaptchaChallengeStatus;
import br.com.hubinfo.captcha.usecase.port.CaptchaChallengeRepositoryPort;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
public class CaptchaChallengePersistenceAdapter implements CaptchaChallengeRepositoryPort {

    private final SpringDataCaptchaChallengeRepository repository;

    public CaptchaChallengePersistenceAdapter(SpringDataCaptchaChallengeRepository repository) {
        this.repository = repository;
    }

    @Override
    public UUID create(UUID serviceRequestId,
                       String cnpj,
                       UUID createdByUserId,
                       String createdByEmail,
                       String pageUrl,
                       String siteKey,
                       String provider,
                       CaptchaChallengeStatus status,
                       Instant createdAt) {

        CaptchaChallengeJpaEntity e = new CaptchaChallengeJpaEntity();
        e.setId(UUID.randomUUID());
        e.setServiceRequestId(serviceRequestId);
        e.setCnpj(cnpj);
        e.setCreatedByUserId(createdByUserId);
        e.setCreatedByEmail(createdByEmail);
        e.setPageUrl(pageUrl);
        e.setSiteKey(siteKey);
        e.setProvider(provider);
        e.setStatus(status.name());
        e.setCreatedAt(createdAt);

        return repository.save(e).getId();
    }

    @Override
    public Optional<CaptchaChallengeView> findById(UUID id) {
        return repository.findById(id).map(e ->
                new CaptchaChallengeView(
                        e.getId(),
                        e.getServiceRequestId(),
                        e.getCnpj(),
                        e.getProvider(),
                        e.getPageUrl(),
                        e.getSiteKey(),
                        CaptchaChallengeStatus.valueOf(e.getStatus()),
                        e.getCreatedAt(),
                        e.getSolvedAt()
                )
        );
    }

    @Override
    public void markSolved(UUID id, String solutionToken, Instant solvedAt) {
        CaptchaChallengeJpaEntity e = repository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("CaptchaChallenge não encontrado: " + id)
        );
        e.setSolutionToken(solutionToken);
        e.setSolvedAt(solvedAt);
        e.setStatus(CaptchaChallengeStatus.SOLVED.name());
        repository.save(e);
    }
}
