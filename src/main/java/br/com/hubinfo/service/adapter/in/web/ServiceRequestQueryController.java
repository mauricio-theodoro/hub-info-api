package br.com.hubinfo.service.adapter.in.web;

import br.com.hubinfo.security.HubInfoPrincipal;
import br.com.hubinfo.service.adapter.in.web.dto.ServiceRequestDetailsResponse;
import br.com.hubinfo.service.adapter.in.web.dto.ServiceRequestListResponse;
import br.com.hubinfo.service.domain.ServiceRequestStatus;
import br.com.hubinfo.service.domain.ServiceType;
import br.com.hubinfo.service.usecase.GetServiceRequestUseCase;
import br.com.hubinfo.service.usecase.ListServiceRequestsUseCase;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Query API para histórico de solicitações do HUB Info.
 *
 * Por que existe:
 * - Evita repetir endpoints por serviço (CND, DT-e, etc.) apenas para "consultar histórico".
 * - Centraliza RBAC (ADMIN vs ME) de forma consistente.
 *
 * Segurança:
 * - Autenticado sempre.
 * - ALL somente ADMIN.
 */
@RestController
@RequestMapping("/api/v1/services/requests")
public class ServiceRequestQueryController {

    private final GetServiceRequestUseCase getUseCase;
    private final ListServiceRequestsUseCase listUseCase;

    public ServiceRequestQueryController(GetServiceRequestUseCase getUseCase,
                                         ListServiceRequestsUseCase listUseCase) {
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    /**
     * GET /api/v1/services/requests/{id}
     *
     * ADMIN: pode ver qualquer ID.
     * USER: só pode ver se o ID pertence a ele.
     */
    @GetMapping("/{id}")
    public ServiceRequestDetailsResponse getById(@PathVariable("id") UUID id,
                                                 @AuthenticationPrincipal HubInfoPrincipal principal,
                                                 Authentication authentication) {

        boolean isAdmin = hasRole(authentication, "ROLE_ADMIN");

        var req = getUseCase.getById(id, principal.userId(), isAdmin);

        return new ServiceRequestDetailsResponse(
                req.id(),
                req.serviceType().name(),
                req.status().name(),
                req.cnpj(),
                req.requestedByUserId(),
                req.requestedByEmail(),
                req.requestedAt(),
                req.completedAt(),
                req.resultCode(),
                req.resultMessage()
        );
    }

    /**
     * GET /api/v1/services/requests?serviceType=&status=&limit=&scope=
     *
     * scope:
     * - ME (default): histórico do usuário autenticado
     * - ALL: histórico geral (apenas ADMIN)
     */
    @GetMapping
    public List<ServiceRequestListResponse> list(@AuthenticationPrincipal HubInfoPrincipal principal,
                                                 Authentication authentication,
                                                 @RequestParam(name = "serviceType", required = false) ServiceType serviceType,
                                                 @RequestParam(name = "status", required = false) ServiceRequestStatus status,
                                                 @RequestParam(name = "scope", defaultValue = "ME") ListServiceRequestsUseCase.Scope scope,
                                                 @RequestParam(name = "limit", defaultValue = "20") @Min(1) @Max(100) int limit) {

        boolean isAdmin = hasRole(authentication, "ROLE_ADMIN");

        var items = listUseCase.list(
                principal.userId(),
                isAdmin,
                scope,
                serviceType,
                status,
                limit
        );

        return items.stream()
                .map(r -> new ServiceRequestListResponse(
                        r.id(),
                        r.serviceType().name(),
                        r.status().name(),
                        r.cnpj(),
                        r.requestedAt(),
                        r.completedAt(),
                        r.resultCode()
                ))
                .toList();
    }

    /**
     * Helper para checar papel sem acoplar regra em vários lugares.
     */
    private static boolean hasRole(Authentication authentication, String role) {
        if (authentication == null || authentication.getAuthorities() == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(a -> role.equals(a.getAuthority()));
    }
}
