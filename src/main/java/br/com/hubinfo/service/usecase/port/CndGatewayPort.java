package br.com.hubinfo.service.usecase.port;

/**
 * Porta para integração com o “mundo externo” (coleta CND).
 *
 * Observação:
 * - Aqui fica o contrato (interface).
 * - A implementação real (WebClient) fica em adapter/out/gateway.
 */
public interface CndGatewayPort {

    CndGatewayResult requestCnd(String normalizedCnpj);

    record CndGatewayResult(
            boolean success,
            String resultCode,        // ex: CAPTCHA_REQUIRED, ISSUED, POSITIVE, UNAVAILABLE
            String message,
            String payloadJson
    ) {}
}
