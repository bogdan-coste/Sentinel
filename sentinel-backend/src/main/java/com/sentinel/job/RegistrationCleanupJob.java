package com.sentinel.job;

import com.sentinel.entity.UserEntity;
import com.sentinel.entity.UserRegistrationEntity;
import com.sentinel.repository.UserRegisterSessionRepository;
import com.sentinel.repository.UserRepository;
import jakarta.transaction.Transactional;
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
        List<UserRegistrationEntity> expired = sessionRepository.findExpiredUnverifiedSessions(LocalDateTime.now());
        for (UserRegistrationEntity session : expired) {
            UserEntity userEntity = session.getUserEntity();
            sessionRepository.delete(session);
            userRepository.delete(userEntity);
        }
    }
}