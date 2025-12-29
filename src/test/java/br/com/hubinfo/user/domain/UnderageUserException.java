package br.com.hubinfo.user.domain;

/**
 * Exceção de domínio: usuário menor de 18 anos.
 */
public class UnderageUserException extends UserDomainException {

    public UnderageUserException(String message) {
        super(message);
    }
}
