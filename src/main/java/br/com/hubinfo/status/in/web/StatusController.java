package br.com.hubinfo.status.in.web;

import java.time.Instant;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint simples para smoke-test.
 *
 * Responsabilidade:
 * - Permitir validar rapidamente que a API está no ar (Postman/monitoramento).
 *
 * Observação:
 * - Esse endpoint não depende de banco, segurança ou integrações.
 */
@RestController
@RequestMapping("/api/v1/status")
public class StatusController {

    @GetMapping
    public Map<String, Object> status() {
        return Map.of(
                "service", "hub-info-api",
                "br/com/hubinfo/status", "UP",
                "timestamp", Instant.now().toString()
        );
    }
}
