package br.com.hubinfo.config;

import br.com.hubinfo.common.time.SystemTimeProvider;
import br.com.hubinfo.common.time.TimeProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração central de beans de infraestrutura leve.
 *
 * Responsabilidade:
 * - Declarar beans transversais (time provider etc.) sem acoplar domínio/usecases ao Spring.
 */
@Configuration
public class AppBeansConfig {

    @Bean
    public TimeProvider timeProvider() {
        return new SystemTimeProvider();
    }
}
