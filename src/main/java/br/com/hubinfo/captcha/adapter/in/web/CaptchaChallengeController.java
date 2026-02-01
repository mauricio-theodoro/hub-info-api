package br.com.hubinfo.captcha.adapter.in.web;

import br.com.hubinfo.captcha.usecase.CaptchaChallengeService;
import br.com.hubinfo.security.HubInfoPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * API do fluxo assistido de CAPTCHA.
 *
 * Fluxo:
 * 1) Agent/UI chama POST para criar o challenge (quando detectar hCaptcha)
 * 2) UI chama GET para obter siteKey/pageUrl
 * 3) Usuário resolve o hCaptcha numa janela do software
 * 4) UI envia o solutionToken para POST /solution
 */
@RestController
@RequestMapping("/api/v1/captcha/challenges")
public class CaptchaChallengeController {

    private final CaptchaChallengeService captchaChallengeService;

    public CaptchaChallengeController(CaptchaChallengeService captchaChallengeService) {
        this.captchaChallengeService = captchaChallengeService;
    }

    @GetMapping("/{id}")
    public CaptchaChallengeService.CaptchaChallengeView get(@PathVariable UUID id) {
        return captchaChallengeService.get(id);
    }

    /**
     * Criação de challenge (útil para testes no Postman e para o Agent real).
     *
     * Importante:
     * - Em produção, o Agent deve criar isso quando DETECTAR o hCaptcha no site alvo.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateCaptchaChallengeResponse create(@RequestBody CreateCaptchaChallengeRequest body,
                                                 @AuthenticationPrincipal HubInfoPrincipal principal) {

        UUID id = captchaChallengeService.createForServiceRequest(
                principal.userId(),
                principal.email(),
                body.serviceType(),
                body.cnpj(),
                body.serviceRequestId(),
                body.provider(),
                body.siteKey(),
                body.pageUrl(),
                body.contextKey()
        );

        return new CreateCaptchaChallengeResponse(id);
    }

    @PostMapping("/{id}/solution")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void submitSolution(@PathVariable UUID id,
                               @RequestBody SubmitCaptchaSolutionRequest body,
                               @AuthenticationPrincipal HubInfoPrincipal principal) {
        captchaChallengeService.submitSolution(
                id,
                principal.userId(),
                principal.email(),
                body.solutionToken()
        );
    }

    public record SubmitCaptchaSolutionRequest(String solutionToken) {}

    public record CreateCaptchaChallengeRequest(
            br.com.hubinfo.service.domain.ServiceType serviceType,
            String cnpj,
            UUID serviceRequestId,
            String provider,
            String siteKey,
            String pageUrl,
            String contextKey
    ) {}

    public record CreateCaptchaChallengeResponse(UUID challengeId) {}
}
