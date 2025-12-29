package br.com.hubinfo.user.usecase.create;

/**
 * Exceção de aplicação (camada de caso de uso).
 *
 * Diferença para domínio:
 * - Domínio: regras "puras" (>=18, e-mail válido etc.).
 * - Use case: orquestração e políticas de aplicação (senha/confirm, e-mail único).
 */
public class UserUseCaseException extends RuntimeException {
    public UserUseCaseException(String message) {
        super(message);
    }
}
