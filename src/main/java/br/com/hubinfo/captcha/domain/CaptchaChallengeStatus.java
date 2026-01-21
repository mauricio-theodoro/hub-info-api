package br.com.hubinfo.captcha.domain;

/**
 * Status do desafio de CAPTCHA.
 *
 * PENDING  -> aguardando resolução humana
 * SOLVED   -> token recebido
 * EXPIRED  -> expirado/invalidado (ex.: token expirou antes do agent consumir)
 */
public enum CaptchaChallengeStatus {
    PENDING,
    SOLVED,
    EXPIRED
}
