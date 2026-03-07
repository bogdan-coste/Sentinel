package com.sentinel.repository;

import com.sentinel.entity.UserLoginEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserLoginSessionRepository extends JpaRepository<UserLoginEntity, Long> {

    UserLoginEntity findBySessionToken(String token);

    @Query("SELECT s FROM UserLoginEntity s WHERE s.sessionToken = :token AND s.expirationDate > :now")
    Optional<UserLoginEntity> findByTokenAndNotExpired(@Param("token") String token, @Param("now")LocalDateTime now);
}
