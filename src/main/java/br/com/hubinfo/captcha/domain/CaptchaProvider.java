package br.com.hubinfo.captcha.domain;

/**
 * Provedor de CAPTCHA.
 *
 * Observação:
 * - No nosso caso, muitas vezes o CAPTCHA aparece em portais do governo
 *   (SEFAZ / e-CAC / Receita). O token pode depender da sessão do navegador
 *   (fluxo assistido).
 */
public enum CaptchaProvider {
    HCAPTCHA,
    RECAPTCHA,
    UNKNOWN
}
