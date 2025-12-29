package br.com.hubinfo.user.domain;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object para e-mail.
 *
 * Responsabilidade:
 * - Garantir que o domínio nunca carregue um e-mail inválido.
 *
 * Por que isso é "Clean"?
 * - Em vez de validar e-mail espalhado em várias camadas, centralizamos a regra no domínio.
 */
public final class EmailAddress {

    /**
     * Regex simples e segura para validação básica.
     * Não tenta cobrir todos os casos do RFC (que é enorme), mas impede entradas claramente inválidas.
     */
    private static final Pattern BASIC_EMAIL =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private final String value;

    private EmailAddress(String value) {
        this.value = value;
    }

    /**
     * Fábrica do Value Object.
     *
     * @param rawEmail e-mail em texto livre vindo de fora (web, integração etc.)
     * @return EmailAddress validado e normalizado
     */
    public static EmailAddress of(String rawEmail) {
        if (rawEmail == null) {
            throw new InvalidEmailException("E-mail é obrigatório.");
        }

        String normalized = rawEmail.trim().toLowerCase();

        if (normalized.isBlank()) {
            throw new InvalidEmailException("E-mail é obrigatório.");
        }

        if (!BASIC_EMAIL.matcher(normalized).matches()) {
            throw new InvalidEmailException("E-mail inválido.");
        }

        return new EmailAddress(normalized);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof EmailAddress other)) return false;
        return Objects.equals(this.value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
