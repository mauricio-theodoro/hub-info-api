package br.com.hubinfo.captcha.adapter.out.persistence;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "captcha_challenges")
public class CaptchaChallengeJpaEntity {

    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "id", nullable = false, length = 36, columnDefinition = "char(36)")
    private UUID id;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "service_request_id", nullable = false, length = 36, columnDefinition = "char(36)")
    private UUID serviceRequestId;

    @Column(name = "cnpj", nullable = false, length = 14, columnDefinition = "char(14)")
    private String cnpj;

    @Column(name = "provider", nullable = false, length = 20)
    private String provider;

    @Column(name = "page_url", nullable = false, length = 500)
    private String pageUrl;

    @Column(name = "site_key", length = 120)
    private String siteKey;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "created_by_user_id", length = 36, columnDefinition = "char(36)")
    private UUID createdByUserId;

    @Column(name = "created_by_email", length = 255)
    private String createdByEmail;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "solved_at")
    private Instant solvedAt;

    /**
     * Token do CAPTCHA resolvido (sensível).
     * No futuro podemos criptografar, mas por ora persistimos para o Agent consumir.
     */
    @Column(name = "solution_token", columnDefinition = "text")
    private String solutionToken;

    protected CaptchaChallengeJpaEntity() {}

    // Getters/Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getServiceRequestId() { return serviceRequestId; }
    public void setServiceRequestId(UUID serviceRequestId) { this.serviceRequestId = serviceRequestId; }
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public String getPageUrl() { return pageUrl; }
    public void setPageUrl(String pageUrl) { this.pageUrl = pageUrl; }
    public String getSiteKey() { return siteKey; }
    public void setSiteKey(String siteKey) { this.siteKey = siteKey; }
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
