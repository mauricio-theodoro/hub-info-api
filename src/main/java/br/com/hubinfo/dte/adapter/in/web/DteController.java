package br.com.hubinfo.dte.adapter.in.web;

import br.com.hubinfo.dte.usecase.RequestDteUseCase;
import br.com.hubinfo.security.HubInfoPrincipal;
import br.com.hubinfo.service.adapter.in.web.dto.ServiceRequestCreateDto;
import br.com.hubinfo.service.adapter.in.web.dto.ServiceRequestCreatedResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Endpoints para DT-e (Caixa Postal) - Federal e Estadual.
 *
 * Neste commit:
 * - Apenas registra a solicitação (status CAPTCHA_REQUIRED).
 * Próximo commit:
 * - Iniciaremos automação/integração.
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
    public ServiceRequestCreatedResponse requestFederal(@Valid @RequestBody ServiceRequestCreateDto body,
                                                        Principal principal) {

        HubInfoPrincipal p = (HubInfoPrincipal) ((org.springframework.security.core.Authentication) principal).getPrincipal();

        var result = useCase.requestFederal(p.userId(), p.email(), body.getCnpj());

        return new ServiceRequestCreatedResponse(
                result.requestId(),
                result.serviceType().name(),
                result.status().name(),
                result.requestedAt()
        );
    }

    @PostMapping("/estadual/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceRequestCreatedResponse requestEstadual(@Valid @RequestBody ServiceRequestCreateDto body,
                                                         Principal principal) {

        HubInfoPrincipal p = (HubInfoPrincipal) ((org.springframework.security.core.Authentication) principal).getPrincipal();

        var result = useCase.requestEstadual(p.userId(), p.email(), body.getCnpj());

        return new ServiceRequestCreatedResponse(
                result.requestId(),
                result.serviceType().name(),
                result.status().name(),
                result.requestedAt()
        );
    }
}
