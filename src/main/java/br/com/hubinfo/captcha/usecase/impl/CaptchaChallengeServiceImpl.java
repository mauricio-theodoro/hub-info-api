package br.com.hubinfo.captcha.usecase.impl;

import br.com.hubinfo.audit.domain.AuditEventType;
import br.com.hubinfo.audit.usecase.AuditService;
import br.com.hubinfo.captcha.domain.CaptchaChallengeStatus;
import br.com.hubinfo.captcha.usecase.CaptchaChallengeService;
import br.com.hubinfo.captcha.usecase.port.CaptchaChallengeRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Implementação do caso de uso de Challenge de CAPTCHA.
 *
 * Responsabilidades:
 * - Expor leitura do desafio (para UI renderizar)
 * - Receber token resolvido (para o Agent prosseguir)
 * - Auditar criação/solução
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
    public CaptchaChallengeRepositoryPort.CaptchaChallengeView get(UUID id) {
        return repository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("CaptchaChallenge não encontrado: " + id)
        );
    }

    @Override
    public void submitSolution(UUID challengeId, String solutionToken) {
        if (solutionToken == null || solutionToken.isBlank()) {
            throw new IllegalArgumentException("Token do CAPTCHA é obrigatório.");
        }

        repository.markSolved(challengeId, solutionToken, Instant.now());

        // Auditoria de resolução (detalhes mínimos).
        auditService.record(
                AuditEventType.CAPTCHA_CHALLENGE_SOLVED,
                null,
                null,
                true,
                "CAPTCHA_CHALLENGE",
                challengeId,
                Map.of("provider", "HCAPTCHA")
        );
    }
}
