package br.com.hubinfo.service.adapter.out.gateway;

import br.com.hubinfo.service.usecase.port.CndGatewayPort;
import org.springframework.stereotype.Component;

/**
 * Implementação temporária (stub) do coletor de CND.
 *
 * Por que stub agora?
 * - Portais oficiais frequentemente exigem CAPTCHA e etapas interativas.
 * - Não vamos implementar automação/bypass de CAPTCHA.
 * - A arquitetura fica pronta e depois plugamos a integração real (manual/sem bypass).
 */
@Component
public class StubCndGateway implements CndGatewayPort {

    @Override
    public CndGatewayResult requestCnd(String normalizedCnpj) {
        return new CndGatewayResult(
                false,
                "CAPTCHA_REQUIRED",
                "Coleta automática não disponível no momento. A emissão exige verificação humana (CAPTCHA).",
                "{\"cnpj\":\"" + normalizedCnpj + "\",\"hint\":\"integracao_pendente\"}"
        );
    }
}
