package com.sentinel.repository;

import com.sentinel.entity.UserRegistrationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public interface UserRegisterSessionRepository extends JpaRepository<UserRegistrationEntity, Long> {

    boolean existsById(Long id);

    @Query("SELECT s FROM UserRegistrationEntity s WHERE s.accountVerified = false AND s.expirationDate < :time")
    List<UserRegistrationEntity> findExpiredUnverifiedSessions(@Param("time") LocalDateTime time);

    Optional<UserRegistrationEntity> findBySessionToken(String token);

}
