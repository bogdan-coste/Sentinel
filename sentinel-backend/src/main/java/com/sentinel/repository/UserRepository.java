package com.sentinel.repository;

import com.sentinel.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByFolderName(String folderName);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
