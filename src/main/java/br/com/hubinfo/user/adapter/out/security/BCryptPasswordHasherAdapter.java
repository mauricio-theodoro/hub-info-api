package br.com.hubinfo.user.adapter.out.security;

import br.com.hubinfo.user.usecase.port.PasswordHasherPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Adapter de hashing de senha usando BCrypt.
 *
 * Por que BCrypt?
 * - É um padrão amplamente aceito para hash de senhas.
 * - Possui "work factor" (cost) configurável.
 *
 * Clean Architecture:
 * - Use cases dependem de PasswordHasherPort.
 * - Esta classe depende de Spring Security (infra) e implementa o port.
 */
@Component
public class BCryptPasswordHasherAdapter implements PasswordHasherPort {

    /**
     * Strength (cost) padrão:
     * - 10 a 12 costuma ser um bom equilíbrio para ambientes corporativos.
     * - Ajustaremos conforme desempenho/ambiente.
     */
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Override
    public String hash(String rawPassword) {
        // Observação: rawPassword não pode ser logada.
        return encoder.encode(rawPassword);
    }
}
