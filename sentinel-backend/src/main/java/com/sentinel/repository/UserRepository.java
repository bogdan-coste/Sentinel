package com.sentinel.repository;

import com.sentinel.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByFolderName(String folderName);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByPublicId(String publicId);

    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByPublicId(String publicId);

    @Query("SELECT u FROM UserEntity u WHERE LOWER(u.username) LIKE LOWER(CONCAT(:query, '%')) OR LOWER(u.name) LIKE LOWER(CONCAT(:query, '%'))")
    List<UserEntity> searchUsers(String query);
}