package br.com.hubinfo.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Segurança temporária (MVP) para permitir testes no Postman com acesso restrito.
 *
 * Estratégia:
 * - HTTP Basic com um usuário ADMIN em memória (credentials via application.yml/env).
 * - Próximo commit: substituir por JWT (login real + users no banco).
 */
@Configuration
public class SecurityConfig {

    @Value("${hubinfo.security.admin.username:admin@hubinfo.local}")
    private String adminUsername;

    @Value("${hubinfo.security.admin.password:Admin@123}")
    private String adminPassword;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // endpoints públicos de diagnóstico / docs
                        .requestMatchers("/api/v1/status").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()

                        // endpoints admin
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                        // qualquer outra rota: autenticada (por enquanto)
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    /**
     * PasswordEncoder padrão para o HTTP Basic (e futuramente para JWT).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Usuário ADMIN em memória (apenas para o MVP enquanto não temos login/JWT).
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        var admin = User.withUsername(adminUsername)
                .password(passwordEncoder.encode(adminPassword))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(admin);
    }
}
