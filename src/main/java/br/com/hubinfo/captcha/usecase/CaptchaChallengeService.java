package br.com.hubinfo.captcha.usecase;

import br.com.hubinfo.captcha.domain.CaptchaChallengeStatus;
import br.com.hubinfo.service.domain.ServiceType;

import java.time.Instant;
import java.util.UUID;

/**
 * Caso de uso do fluxo assistido de CAPTCHA (hCaptcha).
 *
 * Objetivo:
 * - A automação detecta hCaptcha → cria challenge → UI resolve → agente continua.
 */
public interface CaptchaChallengeService {

    CaptchaChallengeView get(UUID id);

    /**
     * Cria um desafio de CAPTCHA vinculado a uma ServiceRequest.
     * Esse método é chamado pelos serviços (DT-e, CNPJReva, e-CAC, SEFAZ, etc.)
     * quando detectarem que a página exige desafio humano.
     */
    UUID createForServiceRequest(UUID actorUserId,
                                 String actorEmail,
                                 ServiceType serviceType,
                                 String cnpj,
                                 UUID serviceRequestId,
                                 String provider,
                                 String siteKey,
                                 String pageUrl,
                                 String contextKey);

    /**
     * Recebe o token resolvido pelo usuário (frontend).
     */
    void submitSolution(UUID challengeId,
                        UUID solvedByUserId,
                        String solvedByEmail,
                        String solutionToken);

    /**
     * View que o Controller expõe para o frontend.
     * Mantemos alinhado ao Port para evitar mapeamento desnecessário.
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
