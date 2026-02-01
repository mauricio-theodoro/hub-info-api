package br.com.hubinfo.security;

import br.com.hubinfo.security.jwt.JwtAuthenticationFilter;
import br.com.hubinfo.security.jwt.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtService jwtService) throws Exception {
        return http
                // API stateless: CSRF não se aplica a REST/Postman
                .csrf(AbstractHttpConfigurer::disable)

                // Desliga mecanismos padrão de login
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // Sem sessão: toda request autenticada via JWT
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Padroniza respostas
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                        .accessDeniedHandler((req, res, e) ->
                                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden"))
                )

                .authorizeHttpRequests(auth -> auth
                        // libera /error (Spring usa em falhas)
                        .requestMatchers("/error").permitAll()

                        // libera recursos estáticos padrões (css/js/img/etc)
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()

                        // libera a pasta de páginas de teste
                        .requestMatchers("/_dev/**").permitAll()

                        // públicos
                        .requestMatchers("/api/v1/status").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()

                        /**
                         * AUTH:
                         * - deixe públicos APENAS login/registro/refresh (ou o que você tiver)
                         * - /me precisa de autenticação (senão principal vem null e vira 500)
                         *
                         * IMPORTANTE: ordem importa. /me deve vir antes do wildcard /auth/**
                         */
                        .requestMatchers(HttpMethod.GET, "/api/v1/auth/me").authenticated()

                        // endpoints públicos de auth (ajuste conforme existir no seu projeto)
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/auth/login",
                                "/api/v1/auth/register",
                                "/api/v1/auth/refresh"
                        ).permitAll()

                        // se você tiver outras rotas públicas dentro de /auth, liste aqui explicitamente
                        // .requestMatchers(HttpMethod.POST, "/api/v1/auth/forgot-password").permitAll()
                        // .requestMatchers(HttpMethod.POST, "/api/v1/auth/reset-password").permitAll()

                        // (opcional) qualquer outra coisa em /auth exige login
                        .requestMatchers("/api/v1/auth/**").authenticated()

                        // admin só ADMIN
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                        // captcha endpoints exigem login
                        .requestMatchers("/api/v1/captcha/**").authenticated()

                        // resto autenticado
                        .anyRequest().authenticated()
                )

                // JWT filter
                .addFilterBefore(new JwtAuthenticationFilter(jwtService), UsernamePasswordAuthenticationFilter.class)

                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
