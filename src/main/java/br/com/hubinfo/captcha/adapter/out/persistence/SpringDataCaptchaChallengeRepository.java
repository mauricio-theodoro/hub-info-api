package br.com.hubinfo.captcha.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataCaptchaChallengeRepository extends JpaRepository<CaptchaChallengeJpaEntity, UUID> {
}
