package br.com.hubinfo.bootstrap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * Valida bootstrap do ADMIN em DEV.
 *
 * Objetivo:
 * - Evitar subir o sistema com seed inválido (hash vazio).
 * - Reduzir "debug infinito" de login falhando por seed incorreto.
 */
@Component
public class AdminBootstrapValidator {

    @Value("${hubinfo.bootstrap.admin.enabled:true}")
    private boolean enabled;

    @Value("${hubinfo.bootstrap.admin.password-hash:}")
    private String passwordHash;

    @PostConstruct
    void validate() {
        if (!enabled) {
            return;
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalStateException(
                    "Bootstrap ADMIN habilitado, mas HUBINFO_BOOTSTRAP_ADMIN_PASSWORD_HASH está vazio. " +
                            "Gere um BCrypt e defina a variável de ambiente."
            );
        }
        if (!passwordHash.startsWith("$2")) {
            throw new IllegalStateException(
                    "Bootstrap ADMIN: password-hash não parece BCrypt válido (deve iniciar com $2...)."
            );
        }
    }
}
