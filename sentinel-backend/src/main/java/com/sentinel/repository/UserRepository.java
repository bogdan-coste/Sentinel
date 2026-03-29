package com.sentinel.repository;

import com.sentinel.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByFolderName(String folderName);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByPublicId(String publicId);

    Optional<User> findByEmail(String email);
    Optional<User> findByPublicId(String publicId);

    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT(:query, '%')) OR LOWER(u.name) LIKE LOWER(CONCAT(:query, '%'))")
    List<User> searchUsers(String query);
}