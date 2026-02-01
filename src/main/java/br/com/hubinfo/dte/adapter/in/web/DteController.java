package br.com.hubinfo.dte.adapter.in.web;

import br.com.hubinfo.dte.usecase.RequestDteUseCase;
import br.com.hubinfo.security.HubInfoPrincipal;
import br.com.hubinfo.service.adapter.in.web.dto.ServiceRequestCreateDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints para solicitar serviços de DT-e (caixa postal).
 */
@RestController
@RequestMapping("/api/v1/services/dte")
public class DteController {

    private final RequestDteUseCase useCase;

    public DteController(RequestDteUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping("/federal/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDteUseCase.Result requestFederal(@AuthenticationPrincipal HubInfoPrincipal principal,
                                                   @Valid @RequestBody ServiceRequestCreateDto body) {
        return useCase.requestFederal(principal.userId(), principal.email(), body.getCnpj());
    }

    @PostMapping("/estadual/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDteUseCase.Result requestEstadual(@AuthenticationPrincipal HubInfoPrincipal principal,
                                                    @Valid @RequestBody ServiceRequestCreateDto body) {
        return useCase.requestEstadual(principal.userId(), principal.email(), body.getCnpj());
    }
}
