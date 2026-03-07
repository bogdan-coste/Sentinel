package com.sentinel.service;

import com.sentinel.entity.UserLoginEntity;
import com.sentinel.repository.UserLoginSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Transactional(readOnly = true)
@Service
public class UserLoginSessionService {

    private final UserLoginSessionRepository repo;

    public UserLoginSessionService(UserLoginSessionRepository repo){
        this.repo = repo;
    }

    public Optional<UserLoginEntity> findValidSession(String token){

        return this.repo.findByTokenAndNotExpired(token, LocalDateTime.now());
    }

    @Transactional
    public void save(UserLoginEntity userLoginEntity){
        repo.save(userLoginEntity);
    }
}
