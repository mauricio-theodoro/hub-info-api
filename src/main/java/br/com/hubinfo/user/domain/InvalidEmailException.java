package br.com.hubinfo.user.domain;

/**
 * Exceção de domínio: e-mail inválido.
 */
public class InvalidEmailException extends UserDomainException {

    public InvalidEmailException(String message) {
        super(message);
    }
}
