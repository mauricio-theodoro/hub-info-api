package br.com.hubinfo.common.web;

import java.time.Instant;
import java.util.Map;

/**
 * Payload padrão de erro (API corporativa).
 */
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, Object> details
) {
}
