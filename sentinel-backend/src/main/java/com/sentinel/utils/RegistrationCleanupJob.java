package com.sentinel.utils;

import com.sentinel.model.User;
import com.sentinel.model.UserRegisterSession;
import com.sentinel.repository.UserRegisterSessionRepository;
import com.sentinel.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class RegistrationCleanupJob {

    private final UserRegisterSessionRepository sessionRepository;

    private final UserRepository userRepository;

    public RegistrationCleanupJob(UserRegisterSessionRepository sessionRepository, UserRepository userRepository){
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @Scheduled(fixedDelay = 3600000)
    public void cleanupExpiredSessions() {
        List<UserRegisterSession> expired = sessionRepository.findExpiredUnverifiedSessions(LocalDateTime.now());
        for (UserRegisterSession session : expired) {
            User user = session.getUser();
            sessionRepository.delete(session);
            userRepository.delete(user);
        }
    }
}