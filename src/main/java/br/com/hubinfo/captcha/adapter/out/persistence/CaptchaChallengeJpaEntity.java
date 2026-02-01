package br.com.hubinfo.captcha.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * Entidade JPA para tabela captcha_challenges.
 *
 * Observação:
 * - status é persistido como STRING (ex.: "PENDING", "SOLVED") para facilitar debug.
 */
@Entity
@Table(name = "captcha_challenges")
public class CaptchaChallengeJpaEntity {

    @Id
    @Column(name = "id", nullable = false, length = 36)
    private UUID id;

    @Column(name = "service_request_id", nullable = false, length = 36)
    private UUID serviceRequestId;

    @Column(name = "cnpj", nullable = false, length = 14)
    private String cnpj;

    @Column(name = "provider", nullable = false, length = 32)
    private String provider;

    @Column(name = "site_key", nullable = false, length = 120)
    private String siteKey;

    @Column(name = "page_url", nullable = false, length = 500)
    private String pageUrl;

    /**
     * Identifica “qual site/tela” originou o desafio.
     * Ex.: "cnpjreva", "sefaz-mg", "ecac", etc.
     */
    @Column(name = "context_key", nullable = true, length = 80)
    private String contextKey;

    @Column(name = "status", nullable = false, length = 32)
    private String status;

    @Column(name = "created_by_user_id", nullable = true, length = 36)
    private UUID createdByUserId;

    @Column(name = "created_by_email", nullable = true, length = 120)
    private String createdByEmail;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "solved_at", nullable = true)
    private Instant solvedAt;

    @Column(name = "solution_token", nullable = true, length = 4000)
    private String solutionToken;

    // ===== getters/setters =====

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getServiceRequestId() { return serviceRequestId; }
    public void setServiceRequestId(UUID serviceRequestId) { this.serviceRequestId = serviceRequestId; }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getSiteKey() { return siteKey; }
    public void setSiteKey(String siteKey) { this.siteKey = siteKey; }

    public String getPageUrl() { return pageUrl; }
    public void setPageUrl(String pageUrl) { this.pageUrl = pageUrl; }

    public String getContextKey() { return contextKey; }
    public void setContextKey(String contextKey) { this.contextKey = contextKey; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public UUID getCreatedByUserId() { return createdByUserId; }
    public void setCreatedByUserId(UUID createdByUserId) { this.createdByUserId = createdByUserId; }

    public String getCreatedByEmail() { return createdByEmail; }
    public void setCreatedByEmail(String createdByEmail) { this.createdByEmail = createdByEmail; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getSolvedAt() { return solvedAt; }
    public void setSolvedAt(Instant solvedAt) { this.solvedAt = solvedAt; }

    public String getSolutionToken() { return solutionToken; }
    public void setSolutionToken(String solutionToken) { this.solutionToken = solutionToken; }
}
