
package br.com.hubinfo.cnpj.adapter.in.web;

import br.com.hubinfo.cnpj.adapter.in.web.dto.CnpjRequestBody;
import br.com.hubinfo.cnpj.usecase.RequestCnpjDataUseCase;
import br.com.hubinfo.security.HubInfoPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    public RequestCnpjDataUseCase.Result request(
            @Valid @RequestBody CnpjRequestBody body,
            @AuthenticationPrincipal HubInfoPrincipal principal
    ) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado.");
        }

        return requestCnpjDataUseCase.request(
                principal.userId(),
                principal.email(),
                body.cnpj()
        );
    }
}
