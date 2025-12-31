package br.com.hubinfo.service.adapter.in.web;

import br.com.hubinfo.security.HubInfoPrincipal;
import br.com.hubinfo.service.adapter.in.web.dto.CndRequestCreateRequest;
import br.com.hubinfo.service.adapter.in.web.dto.ServiceRequestResponse;
import br.com.hubinfo.service.usecase.RequestCndCommand;
import br.com.hubinfo.service.usecase.RequestCndUseCase;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controller do serviço CND.
 *
 * Endpoints:
 * - POST /api/v1/services/cnd/requests
 *
 * Segurança:
 * - Qualquer usuário autenticado pode solicitar.
 * - Auditoria registra quem solicitou e o resultado.
 */
@RestController
@RequestMapping("/api/v1/services/cnd")
public class CndController {

    private final RequestCndUseCase useCase;

    public CndController(RequestCndUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceRequestResponse requestCnd(@AuthenticationPrincipal HubInfoPrincipal principal,
                                             @Valid @RequestBody CndRequestCreateRequest body,
                                             HttpServletRequest request) {

        // Captura metadados HTTP para auditoria (IP, path, UA, etc.)
        RequestCndCommand cmd = new RequestCndCommand(
                body.getCnpj(),
                principal.userId(),
                principal.email(),
                request.getRemoteAddr(),
                request.getMethod(),
                request.getRequestURI(),
                request.getHeader("User-Agent")
        );

        var result = useCase.request(cmd);

        return new ServiceRequestResponse(
                result.id(),
                result.serviceType().name(),
                result.status().name(),
                result.cnpj(),
                result.requestedAt(),
                result.completedAt(),
                result.resultCode(),
                result.resultMessage()
        );
    }
}
