package com.sentinel.repository;

import com.sentinel.model.User;
import com.sentinel.model.UserRegisterSession;
import com.sentinel.utils.TokenGenerator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface UserRegisterSessionRepository extends JpaRepository<UserRegisterSession, Long> {

    boolean existsById(Long id);

    @Query("SELECT s FROM UserRegisterSession s WHERE s.accountVerified = false AND s.expirationDate < :time")
    List<UserRegisterSession> findExpiredUnverifiedSessions(@Param("time") LocalDateTime time);

}
