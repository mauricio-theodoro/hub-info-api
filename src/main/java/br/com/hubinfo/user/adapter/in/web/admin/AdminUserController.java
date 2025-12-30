package br.com.hubinfo.user.adapter.in.web.admin;

import br.com.hubinfo.user.adapter.in.web.admin.dto.CreateUserRequest;
import br.com.hubinfo.user.adapter.in.web.admin.dto.CreateUserResponse;
import br.com.hubinfo.user.usecase.create.CreateUserCommand;
import br.com.hubinfo.user.usecase.create.CreateUserResult;
import br.com.hubinfo.user.usecase.create.CreateUserUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * Endpoints administrativos para gestão de usuários.
 *
 * Observação:
 * - Este controller é uma camada de entrada (adapter/in).
 * - Ele depende apenas do contrato do caso de uso (CreateUserUseCase).
 */
@RestController
@RequestMapping("/api/v1/admin/users")
public class AdminUserController {

    private final CreateUserUseCase createUserUseCase;

    public AdminUserController(CreateUserUseCase createUserUseCase) {
        this.createUserUseCase = createUserUseCase;
    }

    /**
     * Cria um usuário (somente ADMIN).
     *
     * Retorna:
     * - 201 Created
     * - Location: /api/v1/admin/users/{id}
     */
    @PostMapping
    public ResponseEntity<CreateUserResponse> create(@Valid @RequestBody CreateUserRequest request,
                                                     Authentication authentication) {

        // Ainda não temos JWT/login real no banco; então não existe actorUserId confiável.
        // O use case trata null como "system" (UUID nil) no audit.
        CreateUserCommand command = new CreateUserCommand(
                null,
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPassword(),
                request.getPasswordConfirmation(),
                request.getBirthDate()
        );

        CreateUserResult result = createUserUseCase.execute(command);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(result.userId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(new CreateUserResponse(
                        result.userId(),
                        result.firstName(),
                        result.lastName(),
                        result.email(),
                        result.roles(),
                        result.createdAt()
                ));
    }
}
