package br.com.hubinfo.user.usecase.create;

/**
 * Exceção de aplicação: e-mail já cadastrado.
 *
 * Motivo:
 * - Permite mapear corretamente para HTTP 409 (Conflict).
 */
public class EmailAlreadyExistsException extends UserUseCaseException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
