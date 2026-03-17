package com.sentinel.service;

import com.sentinel.model.User;
import com.sentinel.model.UserRegisterSession;
import com.sentinel.repository.UserRegisterSessionRepository;
import com.sentinel.utils.TokenGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserRegistrationSessionService {

    private final UserRegisterSessionRepository userRegisterSessionRepository;
    private final TokenGenerator tokenGenerator;

    public UserRegistrationSessionService(UserRegisterSessionRepository userRegisterSessionRepository, TokenGenerator tokenGenerator){
        this.userRegisterSessionRepository = userRegisterSessionRepository;
        this.tokenGenerator = tokenGenerator;
    }

    public UserRegisterSession createSessionForUser(User user) {
        UserRegisterSession session = new UserRegisterSession();
        session.setSessionToken(tokenGenerator.generateToken());
        session.setExpirationDate(LocalDateTime.now().plusDays(2));
        session.setUser(user);
        return userRegisterSessionRepository.save(session);
    }
}
