package br.com.hubinfo.captcha.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Propriedades do hCaptcha por ServiceType.
 *
 * Exemplo no application.yml:
 * hubinfo:
 *   captcha:
 *     hcaptcha:
 *       challenges:
 *         CNPJ_DADOS_CADASTRAIS:
 *           pageUrl: "https://..."
 *           siteKey: "${HUBINFO_HCAPTCHA_CNPJREVA_SITEKEY:}"
 */
@Component
@ConfigurationProperties(prefix = "hubinfo.captcha.hcaptcha")
public class HcaptchaChallengeProperties {

    private Map<String, Challenge> challenges = new HashMap<>();

    public Map<String, Challenge> getChallenges() {
        return challenges;
    }

    public void setChallenges(Map<String, Challenge> challenges) {
        this.challenges = challenges;
    }

    public static class Challenge {
        private String pageUrl;
        private String siteKey;

        public String getPageUrl() {
            return pageUrl;
        }

        public void setPageUrl(String pageUrl) {
            this.pageUrl = pageUrl;
        }

        public String getSiteKey() {
            return siteKey;
        }

        public void setSiteKey(String siteKey) {
            this.siteKey = siteKey;
        }
    }
}
