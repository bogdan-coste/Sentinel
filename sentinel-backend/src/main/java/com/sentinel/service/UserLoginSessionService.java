package com.sentinel.service;

import com.sentinel.model.UserLoginSession;
import com.sentinel.repository.UserLoginSessionRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserLoginSessionService {

    private final UserLoginSessionRepository repo;

    public UserLoginSessionService(UserLoginSessionRepository repo){
        this.repo = repo;
    }

    public Optional<UserLoginSession> findValidSession(String token){

        return this.repo.findByTokenAndNotExpired(token, LocalDateTime.now());
    }

    public void save(UserLoginSession userLoginSession){
        repo.save(userLoginSession);
    }
}
