package br.com.hubinfo.service.adapter.out.persistence;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "service_requests")
public class ServiceRequestJpaEntity {

    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "id", nullable = false, length = 36, columnDefinition = "char(36)")
    private UUID id;

    @Column(name = "service_type", nullable = false, length = 40)
    private String serviceType;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "cnpj", nullable = false, length = 14, columnDefinition = "char(14)")
    private String cnpj;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "requested_by_user_id", length = 36, columnDefinition = "char(36)")
    private UUID requestedByUserId;

    @Column(name = "requested_by_email", length = 255)
    private String requestedByEmail;

    @Column(name = "requested_at", nullable = false)
    private Instant requestedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "result_code", length = 60)
    private String resultCode;

    @Column(name = "result_message", length = 255)
    private String resultMessage;

    @Column(name = "result_payload_json", columnDefinition = "json")
    private String resultPayloadJson;

    protected ServiceRequestJpaEntity() {}

    // Getters/Setters (clareza)
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public UUID getRequestedByUserId() { return requestedByUserId; }
    public void setRequestedByUserId(UUID requestedByUserId) { this.requestedByUserId = requestedByUserId; }
    public String getRequestedByEmail() { return requestedByEmail; }
    public void setRequestedByEmail(String requestedByEmail) { this.requestedByEmail = requestedByEmail; }
    public Instant getRequestedAt() { return requestedAt; }
    public void setRequestedAt(Instant requestedAt) { this.requestedAt = requestedAt; }
    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
    public String getResultCode() { return resultCode; }
    public void setResultCode(String resultCode) { this.resultCode = resultCode; }
    public String getResultMessage() { return resultMessage; }
    public void setResultMessage(String resultMessage) { this.resultMessage = resultMessage; }
    public String getResultPayloadJson() { return resultPayloadJson; }
    public void setResultPayloadJson(String resultPayloadJson) { this.resultPayloadJson = resultPayloadJson; }
}
