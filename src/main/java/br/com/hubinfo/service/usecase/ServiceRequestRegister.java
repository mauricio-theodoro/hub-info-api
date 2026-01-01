package br.com.hubinfo.service.usecase;

import br.com.hubinfo.service.domain.ServiceRequestStatus;
import br.com.hubinfo.service.domain.ServiceType;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Caso de uso central para registro de solicitações de serviço.
 *
 * Por que existe:
 * - CND, DT-e, Situação Fiscal, Simples Nacional etc. seguem o mesmo fluxo base:
 *   "registrar uma solicitação" -> "processar" -> "resultado".
 * - Centralizando aqui, cada feature só decide o tipo e o payload.
 */
public interface ServiceRequestRegister {

    UUID register(UUID actorUserId,
                  String actorEmail,
                  ServiceType type,
                  Map<String, Object> payload,
                  ServiceRequestStatus status,
                  Instant requestedAt);
}
