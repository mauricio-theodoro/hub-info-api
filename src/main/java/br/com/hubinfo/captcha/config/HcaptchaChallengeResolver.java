package br.com.hubinfo.captcha.config;

import br.com.hubinfo.service.domain.ServiceType;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Resolve a configuração de hCaptcha (siteKey + pageUrl + contextKey) por ServiceType.
 *
 * Por que existe:
 * - Cada portal (CNPJReva, e-CAC, SEFAZ por estado) pode ter um siteKey diferente.
 * - A UI precisa saber qual siteKey renderizar e qual URL abrir para o usuário resolver.
 *
 * Estratégia:
 * - Para cada ServiceType, procuramos propriedades/variáveis de ambiente padronizadas.
 * - Se não estiver configurado, retornamos null (o usecase decide o que fazer).
 */
@Component
public class HcaptchaChallengeResolver {

    /**
     * Configuração mínima para renderizar um desafio hCaptcha.
     *
     * provider   : hoje "HCAPTCHA" (pode ser expandido no futuro)
     * siteKey    : chave pública do hCaptcha (vem do HTML: data-sitekey)
     * pageUrl    : URL do site onde o captcha aparece (abriremos uma janela nesse endereço)
     * contextKey : identificador do contexto (ex.: "CNPJREVA", "ECAC", "SEFAZ_MG"...)
     */
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

        // 1) Primeiro tentamos config específica por serviço:
        // hubinfo.hcaptcha.services.<SERVICE_TYPE>.siteKey / pageUrl / contextKey / provider
        ChallengeConfig byService = resolveByServiceType(serviceType);
        if (byService != null) return byService;

        // 2) Depois tentamos configs "por portal" (atalhos), para não repetir em todos:
        // - CNPJReva (Receita)
        // - e-CAC (Receita)
        // (Você vai expandir com SEFAZ por estado no futuro)
        return switch (serviceType) {

            // DT-e federal/estadual tende a ser via e-CAC
            case DTE_CAIXA_POSTAL_FEDERAL, DTE_CAIXA_POSTAL_ESTADUAL -> resolveEcac();

            // Se você tiver um ServiceType específico para CNPJReva, mapeie aqui.
            // Ex.: case CNPJ_DADOS -> resolveCnpjReva();

            default -> null;
        };
    }

    private ChallengeConfig resolveByServiceType(ServiceType serviceType) {
        String base = "hubinfo.hcaptcha.services." + serviceType.name() + ".";
        String siteKey = env.getProperty(base + "siteKey");
        if (isBlank(siteKey)) return null;

        String pageUrl = env.getProperty(base + "pageUrl");
        String provider = defaultIfBlank(env.getProperty(base + "provider"), "HCAPTCHA");
        String contextKey = defaultIfBlank(env.getProperty(base + "contextKey"), serviceType.name());

        return new ChallengeConfig(provider, siteKey, pageUrl, contextKey);
    }

    private ChallengeConfig resolveCnpjReva() {
        // Pode vir de application.yml OU variável de ambiente (via ${...} no yml)
        String siteKey = env.getProperty("hubinfo.hcaptcha.cnpjreva.siteKey");
        if (isBlank(siteKey)) return null;

        String pageUrl = defaultIfBlank(
                env.getProperty("hubinfo.hcaptcha.cnpjreva.pageUrl"),
                "https://solucoes.receita.fazenda.gov.br/Servicos/cnpjreva/cnpjreva_Solicitacao.asp"
        );

        String provider = defaultIfBlank(env.getProperty("hubinfo.hcaptcha.cnpjreva.provider"), "HCAPTCHA");
        String contextKey = defaultIfBlank(env.getProperty("hubinfo.hcaptcha.cnpjreva.contextKey"), "CNPJREVA");

        return new ChallengeConfig(provider, siteKey, pageUrl, contextKey);
    }

    private ChallengeConfig resolveEcac() {
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
