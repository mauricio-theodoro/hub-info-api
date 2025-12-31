package br.com.hubinfo.service.adapter.out.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface SpringDataServiceRequestRepository extends JpaRepository<ServiceRequestJpaEntity, UUID> {

    /**
     * Query com filtros opcionais (serviceType/status) e userId opcional.
     *
     * - Quando userId for null: lista geral (ADMIN).
     * - Quando userId tiver valor: lista somente daquele usuário.
     */
    @Query("""
           SELECT r
           FROM ServiceRequestJpaEntity r
           WHERE (:userId IS NULL OR r.requestedByUserId = :userId)
             AND (:serviceType IS NULL OR r.serviceType = :serviceType)
             AND (:status IS NULL OR r.status = :status)
           ORDER BY r.requestedAt DESC
           """)
    Page<ServiceRequestJpaEntity> findLatest(@Param("userId") UUID userId,
                                             @Param("serviceType") String serviceType,
                                             @Param("status") String status,
                                             Pageable pageable);
}
