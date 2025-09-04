package com.betul.productmanagement.repository;

import com.betul.productmanagement.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

    Optional<UserSession> findByToken(String token);

    Optional<UserSession> findByUserIdAndDeviceId(Long userId, String deviceId);

    void deleteByUserIdAndDeviceId(Long userId, String deviceId);
}
