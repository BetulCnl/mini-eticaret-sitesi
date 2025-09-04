package com.betul.productmanagement.service;

import com.betul.productmanagement.entity.User;
import com.betul.productmanagement.entity.UserSession;
import com.betul.productmanagement.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSessionService {

    private final UserSessionRepository userSessionRepository;

    // Session kaydetme veya güncelleme
    public UserSession createOrUpdateSession(User user, String deviceId, String token) {
        UserSession session = userSessionRepository
                .findByUserIdAndDeviceId(user.getId(), deviceId)
                .orElse(new UserSession());

        session.setUser(user);
        session.setDeviceId(deviceId);
        session.setToken(token);
        session.setCreatedAt(java.time.LocalDateTime.now());

        return userSessionRepository.save(session);
    }

    public void deleteSessionByTokenAndDevice(String token, String deviceId) {
        userSessionRepository.findByToken(token).ifPresent(session -> {
            if (session.getDeviceId().equals(deviceId)) {
                userSessionRepository.delete(session);
            }
        });
    }
}