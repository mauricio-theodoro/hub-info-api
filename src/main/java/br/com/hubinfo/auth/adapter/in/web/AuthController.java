package br.com.hubinfo.auth.adapter.in.web;

import br.com.hubinfo.auth.adapter.in.web.dto.LoginRequest;
import br.com.hubinfo.auth.adapter.in.web.dto.LoginResponse;
import br.com.hubinfo.auth.adapter.in.web.dto.MeResponse;
import br.com.hubinfo.security.HubInfoPrincipal;
import br.com.hubinfo.security.jwt.JwtService;
import br.com.hubinfo.user.adapter.out.persistence.SpringDataUserRepository;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Controller responsável por autenticação (login) e por retornar
 * informações do usuário autenticado (endpoint /me).
 *
 * Observação de segurança:
 * - O sistema é stateless (JWT). Não existe sessão no servidor.
 * - O cliente deve enviar Authorization: Bearer <token> em toda requisição protegida.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final SpringDataUserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          SpringDataUserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    /**
     * POST /api/v1/auth/login
     *
     * Fluxo:
     * 1) O AuthenticationManager valida e-mail e senha.
     * 2) Buscamos o usuário no banco para obter:
     *    - UUID (usaremos como subject do JWT)
     *    - roles (para autorização nos endpoints)
     * 3) Emitimos o JWT.
     */
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {

        // 1) Autentica credenciais (internamente usa UserDetailsService e PasswordEncoder)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // 2) Carrega o usuário para obter id e roles para o JWT
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();

        UUID userId = user.getId();

        Set<String> roles = parseRoles(user.getRolesCsv());

        // 3) Emite token (subject = userId; claims = email + roles)
        var token = jwtService.issueAccessToken(userId, user.getEmail(), roles);

        return new LoginResponse(token.value(), "Bearer", token.expiresAt());
    }

    /**
     * GET /api/v1/auth/me
     *
     * Antes:
     * - Você pegava o e-mail via authentication.getName()
     * - Consultava o banco para pegar id + roles
     *
     * Agora (recomendado):
     * - O JwtAuthenticationFilter coloca no SecurityContext um principal próprio:
     *   HubInfoPrincipal(userId, email)
     * - Então conseguimos retornar id e email SEM consultar o banco.
     *
     * Sobre roles:
     * - Como roles estão nas authorities do Authentication, podemos retorná-las também
     *   sem ir ao banco.
     */
    @GetMapping("/me")
    public MeResponse me(@AuthenticationPrincipal HubInfoPrincipal principal,
                         Authentication authentication) {

        // principal.userId() e principal.email() vêm do JWT (subject e claim "email").
        UUID userId = principal.userId();
        String email = principal.email();

        // As roles vêm das authorities (ROLE_ADMIN, ROLE_USER, etc.)
        Set<String> roles = authentication.getAuthorities().stream()
                .map(a -> a.getAuthority())             // ex.: "ROLE_ADMIN"
                .map(a -> a.replace("ROLE_", ""))       // vira "ADMIN"
                .collect(Collectors.toUnmodifiableSet());

        return new MeResponse(userId, email, roles);
    }

    /**
     * Utilitário centralizado para converter rolesCsv -> Set<String>.
     * Isso evita duplicação e facilita manutenção.
     */
    private static Set<String> parseRoles(String rolesCsv) {
        if (rolesCsv == null || rolesCsv.isBlank()) {
            return Set.of();
        }
        return Arrays.stream(rolesCsv.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toUnmodifiableSet());
    }
}
