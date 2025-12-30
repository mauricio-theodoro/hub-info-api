package br.com.hubinfo.audit.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataAuditEventRepository extends JpaRepository<AuditEventJpaEntity, UUID> {
}
