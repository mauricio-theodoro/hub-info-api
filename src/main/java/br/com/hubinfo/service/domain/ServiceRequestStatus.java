package br.com.hubinfo.service.domain;

/**
 * Status do ciclo de vida da solicitação.
 *
 * PENDING: registrada, ainda não concluída
 * SUCCESS: concluída com sucesso (CND emitida/retornada)
 * FAILURE: concluída com falha (ex.: captcha, indisponível, bloqueio)
 */
public enum ServiceRequestStatus {
    PENDING,
    SUCCESS,
    FAILURE
}
