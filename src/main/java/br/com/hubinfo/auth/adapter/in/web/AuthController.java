package br.com.hubinfo.auth.adapter.in.web;

import br.com.hubinfo.auth.adapter.in.web.dto.LoginRequest;
import br.com.hubinfo.auth.adapter.in.web.dto.LoginResponse;
import br.com.hubinfo.auth.adapter.in.web.dto.MeResponse;
import br.com.hubinfo.security.jwt.JwtService;
import br.com.hubinfo.user.adapter.out.persistence.SpringDataUserRepository;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final SpringDataUserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, SpringDataUserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();

        Set<String> roles = Arrays.stream(user.getRolesCsv().split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toSet());

        UUID userId = user.getId();

        var token = jwtService.issueAccessToken(userId, user.getEmail(), roles);

        return new LoginResponse(token.value(), "Bearer", token.expiresAt());
    }

    @GetMapping("/me")
    public MeResponse me(Authentication authentication) {
        // Principal é o e-mail (setado pelo JwtAuthenticationFilter)
        String email = authentication.getName();

        var user = userRepository.findByEmail(email).orElseThrow();

        Set<String> roles = Arrays.stream(user.getRolesCsv().split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toSet());

        return new MeResponse(user.getId(), user.getEmail(), roles);
    }
}
