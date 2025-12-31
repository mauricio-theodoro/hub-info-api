package br.com.hubinfo.service.domain;

/**
 * Tipos de serviços do HUB Info.
 *
 * Observação importante:
 * - O HUB Info não é apenas CND.
 * - Vamos evoluir para integrações com e-CAC/SEFAZ, incluindo caixa postal DT-e
 *   (federal e estadual) e outras consultas fiscais.
 *
 * Estratégia:
 * - Este enum cresce conforme adicionamos novos "serviços".
 * - O banco armazena o name() (string). Adicionar novos valores é compatível.
 */
public enum ServiceType {

    // Certidão Negativa de Débitos
    CND,

    // Caixa Postal DT-e (mensagens)
    DTE_CAIXA_POSTAL_FEDERAL,
    DTE_CAIXA_POSTAL_ESTADUAL,

    // Consultas fiscais
    SITUACAO_FISCAL_FEDERAL,
    RELATORIO_SIMPLES_NACIONAL,
    EXTRATO_SIMPLES_NACIONAL
}
