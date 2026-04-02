package com.sentinel.service;

import com.sentinel.entity.UserEntity;
import com.sentinel.entity.UserRegistrationEntity;
import com.sentinel.repository.UserRegisterSessionRepository;
import com.sentinel.utils.TokenGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserRegistrationSessionService {

    private final UserRegisterSessionRepository repo;
    private final TokenGenerator tokenGenerator;

    public UserRegistrationSessionService(UserRegisterSessionRepository repo, TokenGenerator tokenGenerator){
        this.repo = repo;
        this.tokenGenerator = tokenGenerator;
    }

    public UserRegistrationEntity createSessionForUser(UserEntity userEntity) {
        UserRegistrationEntity session = new UserRegistrationEntity();
        session.setSessionToken(tokenGenerator.generateToken());
        session.setExpirationDate(LocalDateTime.now().plusDays(2));
        session.setUserEntity(userEntity);
        return repo.save(session);
    }

    public UserRegistrationEntity findBySessionToken(String token) {
        return repo.findBySessionToken(token)
                .orElseThrow(() -> new RuntimeException("Token invalid"));
    }

    public void save(UserRegistrationEntity userRegistrationEntity){
        repo.save(userRegistrationEntity);
    }
}
