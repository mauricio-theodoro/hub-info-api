package br.com.hubinfo.user.usecase.create;

import br.com.hubinfo.common.audit.AuditEvent;
import br.com.hubinfo.common.audit.AuditEventType;
import br.com.hubinfo.common.time.TimeProvider;
import br.com.hubinfo.user.domain.EmailAddress;
import br.com.hubinfo.user.domain.Role;
import br.com.hubinfo.user.domain.User;
import br.com.hubinfo.user.usecase.port.AuditPort;
import br.com.hubinfo.user.usecase.port.PasswordHasherPort;
import br.com.hubinfo.user.usecase.port.UserRepositoryPort;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Interactor do caso de uso "Criar Usuário".
 *
 * Fluxo (alto nível):
 * 1) Validar comando (camada de aplicação)
 * 2) Normalizar/validar e-mail (via domínio -> EmailAddress)
 * 3) Verificar e-mail único (via port do repositório)
 * 4) Gerar hash da senha (via port)
 * 5) Criar entidade de domínio (valida >=18)
 * 6) Persistir usuário (via port)
 * 7) Publicar auditoria (via port)
 */
public class CreateUserService implements CreateUserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordHasherPort passwordHasher;
    private final AuditPort auditPort;
    private final TimeProvider timeProvider;

    public CreateUserService(
            UserRepositoryPort userRepository,
            PasswordHasherPort passwordHasher,
            AuditPort auditPort,
            TimeProvider timeProvider
    ) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.auditPort = auditPort;
        this.timeProvider = timeProvider;
    }

    @Override
    public CreateUserResult execute(CreateUserCommand command) {
        try {
            validateCommand(command);

            EmailAddress email = EmailAddress.of(command.email());

            if (userRepository.existsByEmail(email)) {
                throw new EmailAlreadyExistsException("Já existe um usuário cadastrado com este e-mail.");
            }

            validatePassword(command.password(), command.passwordConfirmation());

            // Política inicial do MVP:
            // - Usuários criados pelo ADMIN entram como USER.
            // - Depois podemos habilitar criação de ADMIN via endpoint separado e autorização extra.
            Set<Role> roles = Set.of(Role.USER);

            String passwordHash = passwordHasher.hash(command.password());

            User newUser = User.createNew(
                    command.firstName(),
                    command.lastName(),
                    email,
                    passwordHash,
                    command.birthDate(),
                    roles,
                    timeProvider.today(),
                    timeProvider.nowInstant()
            );

            User saved = userRepository.save(newUser);

            auditPort.publish(new AuditEvent(
                    safeActor(command.actorUserId()),
                    AuditEventType.USER_CREATED,
                    timeProvider.nowInstant(),
                    saved.id().toString(),
                    true,
                    Map.of(
                            "email", saved.email().value(),
                            "roles", saved.roles().toString()
                    )
            ));

            return new CreateUserResult(
                    saved.id(),
                    saved.firstName(),
                    saved.lastName(),
                    saved.email().value(),
                    saved.roles().stream().map(Enum::name).collect(java.util.stream.Collectors.toSet()),
                    saved.createdAt()
            );

        } catch (RuntimeException ex) {
            // Auditoria de falha (sem vazar senha; apenas contexto mínimo)
            auditPort.publish(new AuditEvent(
                    safeActor(command != null ? command.actorUserId() : null),
                    AuditEventType.USER_CREATE_FAILED,
                    timeProvider.nowInstant(),
                    command != null ? String.valueOf(command.email()) : "N/A",
                    false,
                    Map.of("error", ex.getMessage())
            ));
            throw ex;
        }
    }

    private static void validateCommand(CreateUserCommand command) {
        if (command == null) {
            throw new UserUseCaseException("Requisição inválida.");
        }
        if (command.firstName() == null || command.firstName().isBlank()) {
            throw new UserUseCaseException("Nome é obrigatório.");
        }
        if (command.lastName() == null || command.lastName().isBlank()) {
            throw new UserUseCaseException("Sobrenome é obrigatório.");
        }
        if (command.email() == null || command.email().isBlank()) {
            throw new UserUseCaseException("E-mail é obrigatório.");
        }
        if (command.birthDate() == null) {
            throw new UserUseCaseException("Data de nascimento é obrigatória.");
        }
    }

    private static void validatePassword(String password, String confirmation) {
        if (password == null || password.isBlank()) {
            throw new UserUseCaseException("Senha é obrigatória.");
        }
        if (confirmation == null || confirmation.isBlank()) {
            throw new UserUseCaseException("Confirmação de senha é obrigatória.");
        }
        if (!password.equals(confirmation)) {
            throw new UserUseCaseException("Senha e confirmação de senha não conferem.");
        }
        if (password.length() < 8) {
            throw new UserUseCaseException("Senha deve ter no mínimo 8 caracteres.");
        }
    }

    /**
     * Em alguns fluxos iniciais, o actor pode não existir ainda (ex.: seed/admin inicial).
     * Mantemos uma UUID fixa (nil) para representar "sistema".
     */
    private static UUID safeActor(UUID actorUserId) {
        return actorUserId != null ? actorUserId : new UUID(0L, 0L);
    }
}
