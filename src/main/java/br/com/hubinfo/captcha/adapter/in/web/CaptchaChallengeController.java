package br.com.hubinfo.captcha.adapter.in.web;

import br.com.hubinfo.captcha.usecase.CaptchaChallengeService;
import br.com.hubinfo.captcha.usecase.port.CaptchaChallengeRepositoryPort;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * API para fluxo "human-in-the-loop" de CAPTCHA.
 *
 * Fluxo:
 * 1) backend cria CaptchaChallenge (status PENDING) ligado a um ServiceRequest
 * 2) o software/UI chama GET para obter siteKey/pageUrl
 * 3) usuário resolve hCaptcha na UI e envia solutionToken via POST /solve
 * 4) Agent consome token e continua a automação (próximos commits)
 */
@RestController
@RequestMapping("/api/v1/captcha/challenges")
public class CaptchaChallengeController {

    private final CaptchaChallengeService service;

    public CaptchaChallengeController(CaptchaChallengeService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public CaptchaChallengeRepositoryPort.CaptchaChallengeView get(@PathVariable UUID id) {
        return service.get(id);
    }

    @PostMapping("/{id}/solve")
    public void solve(@PathVariable UUID id, @Valid @RequestBody SolveRequest body) {
        service.submitSolution(id, body.token());
    }

    public record SolveRequest(@NotBlank String token) {}
}
