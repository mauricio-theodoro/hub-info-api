package br.com.hubinfo.captcha.usecase.impl;

import br.com.hubinfo.audit.domain.AuditEventType;
import br.com.hubinfo.audit.usecase.AuditService;
import br.com.hubinfo.captcha.domain.CaptchaChallengeStatus;
import br.com.hubinfo.captcha.usecase.CaptchaChallengeService;
import br.com.hubinfo.captcha.usecase.port.CaptchaChallengeRepositoryPort;
import br.com.hubinfo.service.domain.ServiceType;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Implementação do caso de uso de Challenge de CAPTCHA.
 *
 * Responsabilidades:
 * - Expor leitura do desafio (para UI renderizar).
 * - Criar desafios quando um serviço detectar hCaptcha.
 * - Receber token resolvido (para o Agent prosseguir).
 * - Auditar criação/solução.
 */
@Service
public class CaptchaChallengeServiceImpl implements CaptchaChallengeService {

    private final CaptchaChallengeRepositoryPort repository;
    private final AuditService auditService;

    public CaptchaChallengeServiceImpl(CaptchaChallengeRepositoryPort repository,
                                       AuditService auditService) {
        this.repository = repository;
        this.auditService = auditService;
    }

    @Override
    public CaptchaChallengeView get(UUID id) {
        var view = repository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("CaptchaChallenge não encontrado: " + id)
        );

        return new CaptchaChallengeView(
                view.id(),
                view.serviceRequestId(),
                view.cnpj(),
                view.provider(),
                view.siteKey(),
                view.pageUrl(),
                view.contextKey(),
                view.status(),
                view.createdAt(),
                view.solvedAt()
        );
    }

    @Override
    public UUID createForServiceRequest(UUID actorUserId,
                                        String actorEmail,
                                        ServiceType serviceType,
                                        String cnpj,
                                        UUID serviceRequestId,
                                        String provider,
                                        String siteKey,
                                        String pageUrl,
                                        String contextKey) {

        // Validações explícitas para evitar lixo no banco.
        if (actorUserId == null) throw new IllegalArgumentException("actorUserId é obrigatório.");
        if (actorEmail == null || actorEmail.isBlank()) throw new IllegalArgumentException("actorEmail é obrigatório.");
        if (serviceType == null) throw new IllegalArgumentException("serviceType é obrigatório.");
        if (cnpj == null || cnpj.isBlank()) throw new IllegalArgumentException("cnpj é obrigatório.");
        if (serviceRequestId == null) throw new IllegalArgumentException("serviceRequestId é obrigatório.");
        if (provider == null || provider.isBlank()) throw new IllegalArgumentException("provider é obrigatório.");
        if (siteKey == null || siteKey.isBlank()) throw new IllegalArgumentException("siteKey é obrigatório.");
        if (pageUrl == null || pageUrl.isBlank()) throw new IllegalArgumentException("pageUrl é obrigatório.");
        if (contextKey == null || contextKey.isBlank()) throw new IllegalArgumentException("contextKey é obrigatório.");

        Instant now = Instant.now();

        // O Port.create(...) exige 10 parâmetros, incluindo serviceRequestId e cnpj, e retorna UUID.
        UUID challengeId = repository.create(
                serviceRequestId,
                cnpj,
                actorUserId,
                actorEmail,
                pageUrl,
                siteKey,
                provider,
                contextKey,
                CaptchaChallengeStatus.PENDING,
                now
        );

        // Auditoria de criação (mínima, mas rastreável).
        auditService.record(
                AuditEventType.CAPTCHA_CHALLENGE_CREATED,
                actorUserId,
                actorEmail,
                true,
                "CAPTCHA_CHALLENGE",
                challengeId,
                Map.of(
                        "serviceType", serviceType.name(),
                        "serviceRequestId", serviceRequestId.toString(),
                        "cnpj", cnpj,
                        "provider", provider,
                        "contextKey", contextKey
                )
        );

        return challengeId;
    }

    @Override
    public void submitSolution(UUID challengeId,
                               UUID solvedByUserId,
                               String solvedByEmail,
                               String solutionToken) {

        if (challengeId == null) throw new IllegalArgumentException("challengeId é obrigatório.");
        if (solvedByUserId == null) throw new IllegalArgumentException("solvedByUserId é obrigatório.");
        if (solvedByEmail == null || solvedByEmail.isBlank()) throw new IllegalArgumentException("solvedByEmail é obrigatório.");
        if (solutionToken == null || solutionToken.isBlank()) {
            throw new IllegalArgumentException("Token do CAPTCHA é obrigatório.");
        }

        repository.markSolved(challengeId, solutionToken, Instant.now());

        auditService.record(
                AuditEventType.CAPTCHA_CHALLENGE_SOLVED,
                solvedByUserId,
                solvedByEmail,
                true,
                "CAPTCHA_CHALLENGE",
                challengeId,
                Map.of("provider", "HCAPTCHA")
        );
    }
}
