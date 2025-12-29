package br.com.hubinfo.user.domain;

/**
 * Perfis de acesso do HUB Info.
 *
 * Observação:
 * - Em Spring Security, roles geralmente são representadas como "ROLE_ADMIN", "ROLE_USER".
 * - Aqui mantemos o enum limpo e a tradução para o framework acontecerá em camadas externas.
 */
public enum Role {
    USER,
    ADMIN
}
