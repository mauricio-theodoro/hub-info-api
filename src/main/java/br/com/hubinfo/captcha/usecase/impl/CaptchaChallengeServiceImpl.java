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
        if (id == null) throw new IllegalArgumentException("id é obrigatório.");

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
        if (isBlank(actorEmail)) throw new IllegalArgumentException("actorEmail é obrigatório.");
        if (serviceType == null) throw new IllegalArgumentException("serviceType é obrigatório.");
        if (isBlank(cnpj)) throw new IllegalArgumentException("cnpj é obrigatório.");
        if (serviceRequestId == null) throw new IllegalArgumentException("serviceRequestId é obrigatório.");
        if (isBlank(provider)) throw new IllegalArgumentException("provider é obrigatório.");
        if (isBlank(siteKey)) throw new IllegalArgumentException("siteKey é obrigatório.");
        if (isBlank(pageUrl)) throw new IllegalArgumentException("pageUrl é obrigatório.");
        if (isBlank(contextKey)) throw new IllegalArgumentException("contextKey é obrigatório.");

        Instant now = Instant.now();

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
                        "contextKey", contextKey,
                        "pageUrl", pageUrl
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
        if (isBlank(solvedByEmail)) throw new IllegalArgumentException("solvedByEmail é obrigatório.");
        if (isBlank(solutionToken)) throw new IllegalArgumentException("Token do CAPTCHA é obrigatório.");

        // 1) Garante que existe e pega dados reais pra auditar corretamente.
        var existing = repository.findById(challengeId).orElseThrow(() ->
                new IllegalArgumentException("CaptchaChallenge não encontrado: " + challengeId)
        );

        // 2) Idempotência: se já estiver resolvido, não sobrescreve token/solvedAt.
        if (existing.status() == CaptchaChallengeStatus.SOLVED) {
            return;
        }

        // 3) Se estiver num status inesperado, melhor falhar explicitamente.
        // (Ajuste se você tiver outros status além de PENDING/SOLVED)
        if (existing.status() != CaptchaChallengeStatus.PENDING) {
            throw new IllegalStateException(
                    "Não é possível submeter solução. Status atual: " + existing.status()
            );
        }

        Instant solvedAt = Instant.now();
        repository.markSolved(challengeId, solutionToken, solvedAt);

        auditService.record(
                AuditEventType.CAPTCHA_CHALLENGE_SOLVED,
                solvedByUserId,
                solvedByEmail,
                true,
                "CAPTCHA_CHALLENGE",
                challengeId,
                Map.of(
                        "provider", existing.provider(),
                        "contextKey", existing.contextKey(),
                        "serviceRequestId", existing.serviceRequestId().toString()
                )
        );
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
