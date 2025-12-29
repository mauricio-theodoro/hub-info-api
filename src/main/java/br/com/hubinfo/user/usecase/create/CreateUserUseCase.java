package br.com.hubinfo.user.usecase.create;

/**
 * Input boundary (contrato) do caso de uso.
 *
 * Observação:
 * - Facilita testes e desacopla controllers de implementações.
 */
public interface CreateUserUseCase {
    CreateUserResult execute(CreateUserCommand command);
}
