package br.com.hubinfo.cnpj.usecase.impl;

import br.com.hubinfo.audit.domain.AuditEventType;
import br.com.hubinfo.audit.usecase.AuditService;
import br.com.hubinfo.captcha.config.HcaptchaChallengeResolver;
import br.com.hubinfo.captcha.usecase.CaptchaChallengeService;
import br.com.hubinfo.cnpj.usecase.RequestCnpjDataUseCase;
import br.com.hubinfo.service.domain.ServiceRequestStatus;
import br.com.hubinfo.service.domain.ServiceType;
import br.com.hubinfo.service.usecase.ServiceRequestRegister;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Caso de uso: solicitar dados cadastrais do CNPJ (fluxo CNPJReva).
 *
 * Commit 16:
 * - registra a ServiceRequest
 * - cria CaptchaChallenge (hCaptcha) e devolve captchaChallengeId
 */
@Service
public class RequestCnpjDataService implements RequestCnpjDataUseCase {

    private final ServiceRequestRegister serviceRequestRegister;
    private final CaptchaChallengeService captchaChallengeService;
    private final HcaptchaChallengeResolver hcaptchaResolver;
    private final AuditService auditService;

    public RequestCnpjDataService(ServiceRequestRegister serviceRequestRegister,
                                  CaptchaChallengeService captchaChallengeService,
                                  HcaptchaChallengeResolver hcaptchaResolver,
                                  AuditService auditService) {
        this.serviceRequestRegister = serviceRequestRegister;
        this.captchaChallengeService = captchaChallengeService;
        this.hcaptchaResolver = hcaptchaResolver;
        this.auditService = auditService;
    }

    @Override
    public Result request(UUID actorUserId, String actorEmail, String cnpj) {
        String cnpjDigits = normalizeCnpj(cnpj);

        ServiceType type = ServiceType.CNPJ_DADOS_CADASTRAIS;
        Instant now = Instant.now();

        // 1) Registra a ServiceRequest
        UUID requestId = serviceRequestRegister.register(
                actorUserId,
                actorEmail,
                type,
                Map.of("cnpj", cnpjDigits),
                ServiceRequestStatus.CAPTCHA_REQUIRED,
                now
        );

        // 2) Auditoria
        auditService.record(
                AuditEventType.SERVICE_REQUEST_CREATED,
                actorUserId,
                actorEmail,
                true,
                "SERVICE_REQUEST",
                requestId,
                Map.of(
                        "serviceType", type.name(),
                        "cnpj", cnpjDigits
                )
        );

        // 3) Cria o CaptchaChallenge (obrigatório no CNPJReva)
        var challengeCfg = hcaptchaResolver.getOrNull(type);
        if (challengeCfg == null) {
            throw new IllegalStateException("Config de hCaptcha não encontrada para: " + type);
        }

        if (isBlank(challengeCfg.siteKey()) || isBlank(challengeCfg.pageUrl())) {
            throw new IllegalStateException(
                    "hCaptcha configurado para " + type + ", mas siteKey/pageUrl estão vazios. " +
                            "Verifique application.yml (hubinfo.captcha.hcaptcha.challenges)."
            );
        }

        UUID captchaChallengeId = captchaChallengeService.createForServiceRequest(
                actorUserId,
                actorEmail,
                type,
                cnpjDigits,
                requestId,
                challengeCfg.provider(),
                challengeCfg.siteKey(),
                challengeCfg.pageUrl(),
                challengeCfg.contextKey()
        );

        return new Result(requestId, type, ServiceRequestStatus.CAPTCHA_REQUIRED, now, captchaChallengeId);
    }

    private static String normalizeCnpj(String cnpj) {
        if (cnpj == null) throw new IllegalArgumentException("CNPJ é obrigatório.");

        String digits = cnpj.replaceAll("\\D", "");
        if (digits.length() != 14) {
            throw new IllegalArgumentException("CNPJ inválido. Esperado 14 dígitos. Recebido: " + digits.length());
        }
        return digits;
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
