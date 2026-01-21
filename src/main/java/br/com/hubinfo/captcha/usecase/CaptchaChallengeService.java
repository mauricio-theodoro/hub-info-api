package br.com.hubinfo.captcha.usecase;

import br.com.hubinfo.captcha.usecase.port.CaptchaChallengeRepositoryPort;

import java.util.UUID;

public interface CaptchaChallengeService {

    CaptchaChallengeRepositoryPort.CaptchaChallengeView get(UUID id);

    /**
     * Recebe o token do hCaptcha resolvido pelo usuário.
     * Esse token será consumido pelo "Agent" de automação para continuar o fluxo.
     */
    void submitSolution(UUID challengeId, String solutionToken);
}
