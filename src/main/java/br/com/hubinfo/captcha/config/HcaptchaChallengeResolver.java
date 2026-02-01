package br.com.hubinfo.captcha.config;

import br.com.hubinfo.service.domain.ServiceType;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Resolve a configuração de hCaptcha (siteKey + pageUrl + contextKey) por ServiceType.
 *
 * Estratégia de resolução (ordem):
 * 1) NOVO (preferencial): hubinfo.captcha.hcaptcha.challenges.<SERVICE_TYPE>.*
 * 2) LEGADO (compat):     hubinfo.hcaptcha.services.<SERVICE_TYPE>.*
 * 3) LEGADO por portal:   hubinfo.hcaptcha.ecac.* (para DT-e, se ainda usar)
 *
 * Observações importantes:
 * - O método getOrNull(...) NÃO lança exceção: retorna null quando não há config.
 *   Quem decide "quebrar" é o usecase (ex.: CNPJReva exige captcha).
 * - Se existir config mas estiver incompleta (ex.: siteKey ok, pageUrl vazio),
 *   retornamos a config mesmo assim. A validação forte fica no usecase.
 */
@Component
public class HcaptchaChallengeResolver {

    public record ChallengeConfig(
            String provider,
            String siteKey,
            String pageUrl,
            String contextKey
    ) {}

    private static final String DEFAULT_PROVIDER = "HCAPTCHA";

    private final Environment env;

    public HcaptchaChallengeResolver(Environment env) {
        this.env = env;
    }

    /**
     * Retorna a configuração do hCaptcha para um tipo de serviço, ou null se não houver config.
     */
    public ChallengeConfig getOrNull(ServiceType serviceType) {
        if (serviceType == null) return null;

        // 1) Preferencial: hubinfo.captcha.hcaptcha.challenges.<SERVICE_TYPE>.*
        ChallengeConfig byChallenges = resolveByChallenges(serviceType);
        if (byChallenges != null) return byChallenges;

        // 2) Compat legado: hubinfo.hcaptcha.services.<SERVICE_TYPE>.*
        ChallengeConfig byServiceLegacy = resolveByServiceTypeLegacy(serviceType);
        if (byServiceLegacy != null) return byServiceLegacy;

        // 3) Compat legado por portal (apenas para DT-e, se aplicável)
        return switch (serviceType) {
            case DTE_CAIXA_POSTAL_FEDERAL, DTE_CAIXA_POSTAL_ESTADUAL -> resolveEcacLegacy();
            default -> null;
        };
    }

    /**
     * NOVO: hubinfo.captcha.hcaptcha.challenges.<SERVICE_TYPE>.*
     *
     * Exemplo (application.yml):
     * hubinfo:
     *   captcha:
     *     hcaptcha:
     *       challenges:
     *         CNPJ_DADOS_CADASTRAIS:
     *           pageUrl: "https://..."
     *           siteKey: "${HUBINFO_HCAPTCHA_CNPJREVA_SITEKEY:}"
     */
    private ChallengeConfig resolveByChallenges(ServiceType serviceType) {
        String base = "hubinfo.captcha.hcaptcha.challenges." + serviceType.name() + ".";

        String siteKey = env.getProperty(base + "siteKey");
        if (isBlank(siteKey)) return null; // sem siteKey => não consideramos configurado

        String pageUrl = env.getProperty(base + "pageUrl");
        String provider = defaultIfBlank(env.getProperty(base + "provider"), DEFAULT_PROVIDER);
        String contextKey = defaultIfBlank(env.getProperty(base + "contextKey"), serviceType.name());

        return new ChallengeConfig(provider, siteKey, pageUrl, contextKey);
    }

    /**
     * LEGADO: hubinfo.hcaptcha.services.<SERVICE_TYPE>.*
     */
    private ChallengeConfig resolveByServiceTypeLegacy(ServiceType serviceType) {
        String base = "hubinfo.hcaptcha.services." + serviceType.name() + ".";

        String siteKey = env.getProperty(base + "siteKey");
        if (isBlank(siteKey)) return null;

        String pageUrl = env.getProperty(base + "pageUrl");
        String provider = defaultIfBlank(env.getProperty(base + "provider"), DEFAULT_PROVIDER);
        String contextKey = defaultIfBlank(env.getProperty(base + "contextKey"), serviceType.name());

        return new ChallengeConfig(provider, siteKey, pageUrl, contextKey);
    }

    /**
     * LEGADO por portal (e-CAC). Útil se algum fluxo antigo ainda usa isso.
     */
    private ChallengeConfig resolveEcacLegacy() {
        String siteKey = env.getProperty("hubinfo.hcaptcha.ecac.siteKey");
        if (isBlank(siteKey)) return null;

        String pageUrl = defaultIfBlank(
                env.getProperty("hubinfo.hcaptcha.ecac.pageUrl"),
                "https://cav.receita.fazenda.gov.br/"
        );

        String provider = defaultIfBlank(env.getProperty("hubinfo.hcaptcha.ecac.provider"), DEFAULT_PROVIDER);
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
