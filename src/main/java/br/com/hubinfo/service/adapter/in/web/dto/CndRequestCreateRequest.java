package br.com.hubinfo.service.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO de entrada para solicitar CND.
 *
 * Observação:
 * - Validamos presença aqui.
 * - A normalização (somente dígitos) ocorre no caso de uso.
 */
public class CndRequestCreateRequest {

    @NotBlank(message = "CNPJ é obrigatório.")
    private String cnpj;

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
}
