package br.com.hubinfo.captcha.config;

import br.com.hubinfo.service.domain.ServiceType;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Resolve a configuração de hCaptcha (siteKey + pageUrl + contextKey) por ServiceType.
 */
@Component
public class HcaptchaChallengeResolver {

    public record ChallengeConfig(
            String provider,
            String siteKey,
            String pageUrl,
            String contextKey
    ) {}

    private final Environment env;

    public HcaptchaChallengeResolver(Environment env) {
        this.env = env;
    }

    /**
     * Retorna a configuração do hCaptcha para um tipo de serviço, ou null se não houver config.
     */
    public ChallengeConfig getOrNull(ServiceType serviceType) {
        if (serviceType == null) return null;

        // hubinfo.captcha.hcaptcha.challenges.<SERVICE_TYPE>.siteKey/pageUrl/provider/contextKey
        ChallengeConfig byChallenges = resolveByChallenges(serviceType);
        if (byChallenges != null) return byChallenges;

        // 2) FALLBACK (legado / antigo): hubinfo.hcaptcha.services.<SERVICE_TYPE>.*
        ChallengeConfig byServiceLegacy = resolveByServiceTypeLegacy(serviceType);
        if (byServiceLegacy != null) return byServiceLegacy;

        // 3) Atalhos por portal (se você quiser usar no futuro)
        return switch (serviceType) {
            case DTE_CAIXA_POSTAL_FEDERAL, DTE_CAIXA_POSTAL_ESTADUAL -> resolveEcacLegacy();
            default -> null;
        };
    }

    private ChallengeConfig resolveByChallenges(ServiceType serviceType) {
        String base = "hubinfo.captcha.hcaptcha.challenges." + serviceType.name() + ".";
        String siteKey = env.getProperty(base + "siteKey");
        if (isBlank(siteKey)) return null;

        String pageUrl = env.getProperty(base + "pageUrl");
        String provider = defaultIfBlank(env.getProperty(base + "provider"), "HCAPTCHA");
        String contextKey = defaultIfBlank(env.getProperty(base + "contextKey"), serviceType.name());

        return new ChallengeConfig(provider, siteKey, pageUrl, contextKey);
    }

    // ---- LEGADO (se quiser manter compatibilidade) ----

    private ChallengeConfig resolveByServiceTypeLegacy(ServiceType serviceType) {
        String base = "hubinfo.hcaptcha.services." + serviceType.name() + ".";
        String siteKey = env.getProperty(base + "siteKey");
        if (isBlank(siteKey)) return null;

        String pageUrl = env.getProperty(base + "pageUrl");
        String provider = defaultIfBlank(env.getProperty(base + "provider"), "HCAPTCHA");
        String contextKey = defaultIfBlank(env.getProperty(base + "contextKey"), serviceType.name());

        return new ChallengeConfig(provider, siteKey, pageUrl, contextKey);
    }

    private ChallengeConfig resolveEcacLegacy() {
        String siteKey = env.getProperty("hubinfo.hcaptcha.ecac.siteKey");
        if (isBlank(siteKey)) return null;

        String pageUrl = defaultIfBlank(env.getProperty("hubinfo.hcaptcha.ecac.pageUrl"), "https://cav.receita.fazenda.gov.br/");
        String provider = defaultIfBlank(env.getProperty("hubinfo.hcaptcha.ecac.provider"), "HCAPTCHA");
        String contextKey = defaultIfBlank(env.getProperty("hubinfo.hcaptcha.ecac.contextKey"), "ECAC");

        return new ChallengeConfig(provider, siteKey, pageUrl, contextKey);
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private static String defaultIfBlank(String value, String def) {
        return isBlank(value) ? def : value;
    }
}
