package br.com.hubinfo.auth.adapter.in.web.dto;

import java.time.Instant;

public record LoginResponse(
        String accessToken,
        String tokenType,
        Instant expiresAt
) {
}
