package br.com.hubinfo.captcha.domain;

/**
 * Status do desafio de CAPTCHA.
 *
 * Regras:
 * - PENDING: desafio criado e aguardando solução do usuário.
 * - SOLVED: usuário enviou o token (h-captcha-response) e o sistema pode prosseguir.
 * - EXPIRED: desafio perdeu validade (ex.: usuário demorou, sessão expirou, reprocesso necessário).
 */
/**
 * Status do desafio de CAPTCHA.
 */
public enum CaptchaChallengeStatus {
    PENDING,
    SOLVED
}
