package com.sentinel.repository;

import com.sentinel.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByFolderName(String folderName);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByPublicId(String publicId);

    Optional<User> findByEmail(String email);
    Optional<User> findByPublicId(String publicId);
}