package br.com.hubinfo.user.adapter.in.web.admin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * DTO de entrada para criação de usuário pelo ADMIN.
 *
 * Observação:
 * - Validações aqui evitam requests obviamente inválidos.
 * - Regras de negócio fortes (>= 18) continuam no domínio/usecase.
 */
public class CreateUserRequest {

    @NotBlank(message = "Nome é obrigatório.")
    private String firstName;

    @NotBlank(message = "Sobrenome é obrigatório.")
    private String lastName;

    @NotBlank(message = "E-mail é obrigatório.")
    @Email(message = "E-mail inválido.")
    private String email;

    @NotBlank(message = "Senha é obrigatória.")
    @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres.")
    private String password;

    @NotBlank(message = "Confirmação de senha é obrigatória.")
    private String passwordConfirmation;

    @NotNull(message = "Data de nascimento é obrigatória.")
    @Past(message = "Data de nascimento deve estar no passado.")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    // getters/setters

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPasswordConfirmation() { return passwordConfirmation; }
    public void setPasswordConfirmation(String passwordConfirmation) { this.passwordConfirmation = passwordConfirmation; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
}
