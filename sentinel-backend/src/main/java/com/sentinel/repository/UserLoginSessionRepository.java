package com.sentinel.repository;

import com.sentinel.model.User;
import com.sentinel.model.UserLoginSession;
import com.sentinel.model.UserRegisterSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserLoginSessionRepository extends JpaRepository<UserLoginSession, Long> {

    UserLoginSession findBySessionToken(String token);

    @Query("SELECT s FROM UserLoginSession s WHERE s.sessionToken = :token AND s.expirationDate > :now")
    public Optional<UserLoginSession> findByTokenAndNotExpired(@Param("token") String token, @Param("now")LocalDateTime now);
}
