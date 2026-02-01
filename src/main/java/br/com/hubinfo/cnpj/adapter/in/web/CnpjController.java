package br.com.hubinfo.cnpj.adapter.in.web;

import br.com.hubinfo.cnpj.adapter.in.web.dto.CnpjRequestBody;
import br.com.hubinfo.cnpj.usecase.RequestCnpjDataUseCase;
import br.com.hubinfo.security.HubInfoPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * API: Solicitação de dados cadastrais do CNPJ (CNPJReva).
 */
@RestController
@RequestMapping("/api/v1/services/cnpj")
public class CnpjController {

    private final RequestCnpjDataUseCase requestCnpjDataUseCase;

    public CnpjController(RequestCnpjDataUseCase requestCnpjDataUseCase) {
        this.requestCnpjDataUseCase = requestCnpjDataUseCase;
    }

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestCnpjDataUseCase.Result request(@Valid @RequestBody CnpjRequestBody body,
                                                 HubInfoPrincipal principal) {

        return requestCnpjDataUseCase.request(
                principal.userId(),
                principal.email(),
                body.cnpj()
        );
    }
}
