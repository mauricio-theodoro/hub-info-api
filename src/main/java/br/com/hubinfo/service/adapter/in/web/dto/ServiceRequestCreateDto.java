package br.com.hubinfo.service.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO padrão para criação de solicitações por CNPJ.
 *
 * Observação:
 * - Mais serviços virão a precisar de parâmetros extras. Para esses casos,
 *   criaremos DTOs específicos por serviço.
 */
public class ServiceRequestCreateDto {

    @NotBlank(message = "CNPJ é obrigatório.")
    private String cnpj;

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
}
