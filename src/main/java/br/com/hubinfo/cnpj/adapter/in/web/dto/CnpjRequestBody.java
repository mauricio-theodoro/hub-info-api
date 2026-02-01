package br.com.hubinfo.cnpj.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record CnpjRequestBody(
        @NotBlank String cnpj
) {}
