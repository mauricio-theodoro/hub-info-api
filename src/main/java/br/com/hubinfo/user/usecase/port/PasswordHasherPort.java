package br.com.hubinfo.user.usecase.port;

/**
 * Port para hashing de senha.
 *
 * Responsabilidade:
 * - Converter senha em texto (raw) para um hash seguro (ex.: BCrypt).
 *
 * Observação:
 * - O domínio recebe apenas passwordHash.
 * - O algoritmo e a biblioteca ficam na infra (adapter).
 */
public interface PasswordHasherPort {
    String hash(String rawPassword);
}
