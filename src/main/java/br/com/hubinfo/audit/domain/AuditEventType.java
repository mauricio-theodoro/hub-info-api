package br.com.hubinfo.audit.domain;

public enum AuditEventType {
    USER_CREATED,
    AUTH_LOGIN_SUCCESS,
    AUTH_LOGIN_FAILURE,

    // Base pronta para serviços (Commit futuro: CND etc.)
    SERVICE_REQUESTED,
    SERVICE_REQUEST_SUCCESS,
    SERVICE_REQUEST_FAILURE
}
