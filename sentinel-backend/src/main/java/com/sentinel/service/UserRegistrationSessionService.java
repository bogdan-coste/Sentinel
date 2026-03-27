package com.sentinel.service;

import com.sentinel.model.User;
import com.sentinel.model.UserRegisterSession;
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

    public UserRegisterSession createSessionForUser(User user) {
        UserRegisterSession session = new UserRegisterSession();
        session.setSessionToken(tokenGenerator.generateToken());
        session.setExpirationDate(LocalDateTime.now().plusDays(2));
        session.setUser(user);
        return repo.save(session);
    }

    public UserRegisterSession findBySessionToken(String token) {
        return repo.findBySessionToken(token)
                .orElseThrow(() -> new RuntimeException("Token invalid"));
    }

    public void save(UserRegisterSession userRegisterSession){
        repo.save(userRegisterSession);
    }
}
