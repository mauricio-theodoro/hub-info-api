package br.com.hubinfo.common.time;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Provedor de tempo (abstração).
 *
 * Motivo:
 * - Evitar dependência direta de clock do sistema dentro do domínio/casos de uso.
 * - Facilitar testes determinísticos (sem "flakiness" por horário real).
 */
public interface TimeProvider {
    Instant nowInstant();
    LocalDate today();
}
