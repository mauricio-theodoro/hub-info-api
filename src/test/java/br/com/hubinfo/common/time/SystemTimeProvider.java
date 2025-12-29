package br.com.hubinfo.common.time;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Implementação padrão de TimeProvider usando o relógio do sistema.
 *
 * Observação:
 * - ZoneId pode ser padronizado aqui. Para o Brasil, usamos America/Sao_Paulo.
 * - Em produção, isso evita divergências de data por timezone.
 */
public class SystemTimeProvider implements TimeProvider {

    private static final ZoneId ZONE_ID = ZoneId.of("America/Sao_Paulo");

    @Override
    public Instant nowInstant() {
        return Instant.now();
    }

    @Override
    public LocalDate today() {
        return LocalDate.now(ZONE_ID);
    }
}
