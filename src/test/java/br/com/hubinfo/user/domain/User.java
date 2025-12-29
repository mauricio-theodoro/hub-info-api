package br.com.hubinfo.user.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Entidade de domínio: User.
 *
 * Responsabilidade:
 * - Representar um usuário válido dentro do domínio do HUB Info.
 * - Aplicar regras de negócio essenciais:
 *   1) Usuário precisa ter nome/sobrenome
 *   2) E-mail válido (via EmailAddress)
 *   3) Data de nascimento obrigatória
 *   4) Usuário deve ser maior ou igual a 18 anos (no momento do cadastro)
 *
 * Observação:
 * - Esta classe NÃO depende de Spring, JPA, Jackson, etc.
 * - Persistência e serialização ficam em camadas externas (adapters).
 */
public final class User {

    private final UUID id;
    private final String firstName;
    private final String lastName;
    private final EmailAddress email;
    private final String passwordHash;
    private final LocalDate birthDate;
    private final Set<Role> roles;
    private final Instant createdAt;

    private User(
            UUID id,
            String firstName,
            String lastName,
            EmailAddress email,
            String passwordHash,
            LocalDate birthDate,
            Set<Role> roles,
            Instant createdAt
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.birthDate = birthDate;
        this.roles = roles;
        this.createdAt = createdAt;
    }

    /**
     * Fábrica de criação de usuário (regra de entrada do domínio).
     *
     * Importante:
     * - Recebe passwordHash (senha já processada) porque hashing é responsabilidade do caso de uso/infra.
     * - Recebe "today" e "createdAt" para evitar dependência em relógio do sistema dentro do domínio.
     */
    public static User createNew(
            String firstName,
            String lastName,
            EmailAddress email,
            String passwordHash,
            LocalDate birthDate,
            Set<Role> roles,
            LocalDate today,
            Instant createdAt
    ) {
        validateNames(firstName, lastName);
        validatePasswordHash(passwordHash);
        validateBirthDateAndAge(birthDate, today);

        if (roles == null || roles.isEmpty()) {
            throw new UserDomainException("Usuário deve possuir ao menos um perfil (USER/ADMIN).");
        }

        UUID id = UUID.randomUUID();

        // Set imutável para garantir integridade do agregado no domínio.
        Set<Role> safeRoles = Collections.unmodifiableSet(Set.copyOf(roles));

        return new User(
                id,
                firstName.trim(),
                lastName.trim(),
                email,
                passwordHash,
                birthDate,
                safeRoles,
                createdAt
        );
    }

    private static void validateNames(String firstName, String lastName) {
        if (firstName == null || firstName.trim().isBlank()) {
            throw new UserDomainException("Nome é obrigatório.");
        }
        if (lastName == null || lastName.trim().isBlank()) {
            throw new UserDomainException("Sobrenome é obrigatório.");
        }
    }

    private static void validatePasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new UserDomainException("Senha (hash) é obrigatória.");
        }
        // Observação:
        // - Aqui não validamos formato do hash (BCrypt, Argon2 etc).
        // - Essa validação específica pertence ao componente de hashing.
    }

    /**
     * Regra: somente maiores (>= 18) podem se cadastrar.
     */
    private static void validateBirthDateAndAge(LocalDate birthDate, LocalDate today) {
        if (birthDate == null) {
            throw new UserDomainException("Data de nascimento é obrigatória.");
        }
        if (today == null) {
            throw new UserDomainException("Data de referência (today) é obrigatória.");
        }

        if (birthDate.isAfter(today)) {
            throw new UserDomainException("Data de nascimento não pode ser no futuro.");
        }

        int age = Period.between(birthDate, today).getYears();
        if (age < 18) {
            throw new UnderageUserException("Apenas maiores de 18 anos podem se cadastrar.");
        }
    }

    // Getters (sem Lombok no domínio para manter clareza e evitar dependências)

    public UUID id() {
        return id;
    }

    public String firstName() {
        return firstName;
    }

    public String lastName() {
        return lastName;
    }

    public EmailAddress email() {
        return email;
    }

    public String passwordHash() {
        return passwordHash;
    }

    public LocalDate birthDate() {
        return birthDate;
    }

    public Set<Role> roles() {
        return roles;
    }

    public Instant createdAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User other)) return false;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
