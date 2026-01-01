package br.com.hubinfo.service.domain;

/**
 * Status de uma solicitação de serviço (service_requests).
 *
 * Importante:
 * - Persistimos o status como String usando enum.name().
 * - Isso permite evoluir adicionando novos valores sem quebrar compatibilidade.
 *
 * Fluxo típico:
 * - PENDING (registrado)
 * - CAPTCHA_REQUIRED (bloqueado por captcha/mfa)
 * - SUCCESS (concluído)
 * - FAILURE (falhou)
 */
public enum ServiceRequestStatus {

    /**
     * Solicitação registrada e aguardando processamento.
     */
    PENDING,

    /**
     * Solicitação não pode ser processada automaticamente porque exige CAPTCHA/MFA.
     */
    CAPTCHA_REQUIRED,

    /**
     * Solicitação concluída com sucesso.
     */
    SUCCESS,

    /**
     * Solicitação concluída com falha (erro técnico, impedimento, indisponibilidade, etc.).
     */
    FAILURE
}
