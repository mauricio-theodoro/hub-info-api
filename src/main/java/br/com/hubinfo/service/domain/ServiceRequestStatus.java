package br.com.hubinfo.service.domain;

/**
 * Status de uma solicitação de serviço.
 *
 * Importante:
 * - Mantemos um workflow simples e extensível para rastreabilidade e auditoria.
 */
public enum ServiceRequestStatus {

    RECEIVED,
    PROCESSING,

    /**
     * Quando o serviço exige interação humana (ex.: CAPTCHA, MFA, certificado, etc.).
     */
    CAPTCHA_REQUIRED,

    SUCCESS,
    FAILED
}
