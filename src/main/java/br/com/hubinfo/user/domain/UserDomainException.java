package br.com.hubinfo.user.domain;

/**
 * Exceção base do domínio de Usuário.
 *
 * Responsabilidade:
 * - Representar violações de regra de negócio do domínio (ex.: menor de idade, e-mail inválido).
 *
 * Observação de Clean Architecture:
 * - Exceções do domínio não dependem de frameworks.
 * - A camada web (controller) decide como traduzir isso em HTTP (400, 422, etc.).
 */
public class UserDomainException extends RuntimeException {

    public UserDomainException(String message) {
        super(message);
    }
}
