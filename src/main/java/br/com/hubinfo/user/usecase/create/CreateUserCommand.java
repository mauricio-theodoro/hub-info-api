package br.com.hubinfo.user.usecase.create;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Modelo de entrada do caso de uso "Criar Usuário".
 *
 * Observação:
 * - O ADMIN vai fornecer estes dados na aba de cadastro.
 * - A camada web (controller) vai montar este command.
 */
public record CreateUserCommand(
        UUID actorUserId,
        String firstName,
        String lastName,
        String email,
        String password,
        String passwordConfirmation,
        LocalDate birthDate
) {
}
