package br.com.hubinfo.security;

import br.com.hubinfo.user.adapter.out.persistence.SpringDataUserRepository;
import br.com.hubinfo.user.adapter.out.persistence.UserJpaEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Carrega usuários do banco para autenticação.
 */
@Service
public class DbUserDetailsService implements UserDetailsService {

    private final SpringDataUserRepository repository;

    public DbUserDetailsService(SpringDataUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserJpaEntity user = repository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));

        var authorities = Arrays.stream(user.getRolesCsv().split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                .collect(Collectors.toSet());

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(authorities)
                .build();
    }
}
