package br.com.hubinfo.config;

import br.com.hubinfo.common.time.TimeProvider;
import br.com.hubinfo.user.usecase.create.CreateUserService;
import br.com.hubinfo.user.usecase.create.CreateUserUseCase;
import br.com.hubinfo.user.usecase.port.AuditPort;
import br.com.hubinfo.user.usecase.port.PasswordHasherPort;
import br.com.hubinfo.user.usecase.port.UserRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração de wiring do feature USER.
 *
 * Clean Architecture:
 * - Controllers dependem de CreateUserUseCase (interface).
 * - Implementação concreta (CreateUserService) é registrada aqui.
 */
@Configuration
public class UserUseCaseConfig {

    @Bean
    public CreateUserUseCase createUserUseCase(
            UserRepositoryPort userRepositoryPort,
            PasswordHasherPort passwordHasherPort,
            AuditPort auditPort,
            TimeProvider timeProvider
    ) {
        return new CreateUserService(userRepositoryPort, passwordHasherPort, auditPort, timeProvider);
    }
}
