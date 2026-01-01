package br.com.hubinfo.service.domain;

/**
 * Tipos de serviços do HUB Info.
 *
 * Regras:
 * - O banco persiste o name() (string). Portanto, NÃO renomeie valores já usados em produção/dev.
 * - Novos serviços devem ser adicionados como novos enums.
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
    EXTRATO_SIMPLES_NACIONAL;

    /**
     * Indica se o serviço tende a exigir desafios anti-robô (CAPTCHA) ou interação humana.
     * Uso:
     * - API pode responder com status CAPTCHA_REQUIRED de forma padronizada.
     * - Frontend pode tratar UX (ex.: abrir fluxo assistido).
     */
    public boolean isInteractiveLikely() {
        return this == DTE_CAIXA_POSTAL_FEDERAL || this == DTE_CAIXA_POSTAL_ESTADUAL;
    }

    /**
     * Categoria do serviço para relatórios e auditoria.
     */
    public ServiceCategory category() {
        if (this == CND) return ServiceCategory.CERTIDAO;
        if (this.name().startsWith("DTE_")) return ServiceCategory.CAIXA_POSTAL;
        return ServiceCategory.CONSULTA_FISCAL;
    }

    public enum ServiceCategory {
        CERTIDAO,
        CAIXA_POSTAL,
        CONSULTA_FISCAL
    }
}
